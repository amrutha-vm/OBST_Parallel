//mpijavac -cp ~/openmpi-4.0.5/ompi/mpi/java/java/mpi.jar ParallelOBST.java
//mpiexec java -cp ~/openmpi-4.0.5/ompi/mpi/java/java/mpi.jar ParallelOBST

/**
 * author = "Amrutha Varshini Mandalreddy"
 */

import mpi.MPI;
import mpi.MPIException;

import java.io.*;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.Random;

public class ParallelOBST {
    private int N;
    private Double[] probabilities;
    public Double result;
    public int processorCount;

    /**
     * Constructore to intialize ParallelOBST and load probabilities from file
     *
     * @param N
     * @param processorCount
     * @param probabilitiesFile
     * @throws IOException
     */
    ParallelOBST(int N, int processorCount, String probabilitiesFile) throws IOException {
        this.N = N;
        this.processorCount = processorCount;
        int rem = N % processorCount;
        if (rem != 0) {
            this.N = N + rem;
        }
        this.probabilities = loadProbabilities(N, rem, probabilitiesFile);
    }

    /**
     * Constructor to initialize ParallelBOST and generate probabilities
     * @param N
     * @param processorCount
     */
    ParallelOBST(int N, int processorCount) {
        this.N = N;
        this.processorCount = processorCount;
        int rem = N % processorCount;
        if (rem != 0) {
            this.N = N + rem;
        }
        this.probabilities = generateProbabilities();
    }

    /**
     * Function to load probabilities from file
     *
     * @param N
     * @param rem
     * @param probabilitiesFile
     * @return
     * @throws IOException
     */
    public Double[] loadProbabilities(int N, int rem, String probabilitiesFile) throws IOException {
        probabilities = new Double[N + rem];
        java.io.File file = new File(probabilitiesFile);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = "";
        while ((s = bufferedReader.readLine()) != null) {
            String[] strings = s.split(" ");
            for (int i = 0; i < strings.length; i++) {
                probabilities[i] = Double.parseDouble(strings[i]);
            }
        }
        for (int i = N; i < N + rem; i++) {
            probabilities[i] = 0.0;
        }
        return probabilities;
    }

    /**
     * Generate probabilities of length N if not loading from a file
     * @return
     */
    public Double[]  generateProbabilities() {
        Double[] probabilities = new Double[N];
        Random random = new Random();
        Double min = 0.0;
        Double max = 1.0;
        for (int i = 0; i < N; i++) {
            Double current = min + random.nextFloat() * (max - min);
            probabilities[i] = current;
            max = max - current;
        }
        return probabilities;
    }


    /**
     * Function to print a 2-D array to debug the code
     *
     * @param arr
     * @param rank
     */
    public void printArray(Double[][] arr, int rank) {
        String ss = "";
        for (int i = 0; i < arr.length; i++) {
            ss = ss + Arrays.toString(arr[i]) + ",";
        }
        System.out.println("Rank " + rank + " OPT value is " + ss);
    }

    /**
     * Function returns the sum pf probabilities from P_i to P_j
     *
     * @param i
     * @param j
     * @return
     */
    public Double probabilitySUM(int i, int j) {
        Double sum = 0.0;
        for (int k = i; k <= j; k++) {
            sum = sum + probabilities[k];
        }
        return sum;
    }

    /**
     * Function to calculate OPT[i][j] using OPT array
     *
     * @param i
     * @param j
     * @param OPT
     * @return
     */
    public Double getOPT(int i, int j, Double[][] OPT) {
        if (i < 0 || j < 0 || i >= N || j >= N) {
            return 0.0;
        }
        if (OPT[i][j] == null) {
            if (j < i) {
                OPT[i][j] = 0.0;
                return 0.0;
            }
            Double min = 100000000.0;
            Double probabilitySUM = probabilitySUM(i, j);
            for (int k = i; k <= j; k++) {
                // Double temp = getOPT(i, k - 1, OPT) + getOPT(k + 1, j, OPT) + probabilitySUM;
                double t1 = 0.0, t2 = 0.0;
                if (k - 1 < N && k >= 1) {
                    t1 = OPT[i][k - 1];
                }
                if (k + 1 < N && k + 1 >= 0) {
                    t2 = OPT[k + 1][j];
                }

                Double temp = t1 + t2 + probabilitySUM;
                if (temp < min) {
                    min = temp;
                }
            }
            OPT[i][j] = min;
            return min;
        } else {
            return OPT[i][j];
        }
    }

    /**
     * Function to execute creating OBST in parallel
     *
     * @param args
     * @throws MPIException
     */
    public void executeParallel(String[] args) throws MPIException {
        int n = N / processorCount;
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.getRank();
        int tag = 0;
        if (rank == 0) {
            DoubleBuffer buffer = MPI.newDoubleBuffer(n * N);
            Double[][] OPT = new Double[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < i; j++) {
                    OPT[i][j] = 0.0;
                }
            }
            int start = 0;
            int end = n - 1;
            int temp = start + n;
            // for all columns
            for (int j = start; j < N; j++) {
                // Processor 0 should receive items items from Processor 1 , processorCount-1 times
                if (j == temp) {
                    MPI.COMM_WORLD.iRecv(buffer, (temp - start) * n, MPI.DOUBLE, 1, tag);

                    //Put buffer values into OPT
                    for (int k = 0; k < (temp - start); k++) {
                        for (int m = 0; m < n; m++) {
                            OPT[end + 1 + k][j + m] = buffer.get();
                        }
                    }
                    buffer.clear();
                    temp = temp + n;

                }
                for (int i = Math.min(j, end); i >= 0; i--) {
                    getOPT(i, j, OPT);
                }

            }
            result = OPT[0][N - 1];
            System.out.println("Result " + OPT[0][N - 1]);

        } else {
            Double[][] OPT = new Double[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < i; j++) {
                    OPT[i][j] = 0.0;
                }
            }
            int start = n * rank;
            int end = (n * (rank + 1)) - 1;
            if (rank == processorCount - 1) {
                end = N - 1;
            }
            int temp = start + n;
            DoubleBuffer sBuffer = MPI.newDoubleBuffer(n * N);
            DoubleBuffer rBuffer = MPI.newDoubleBuffer(n * N);
            // for columns from (start, N)
            for (int j = start; j <= N; j++) {
                if (j == temp && j <= N) {
                    sBuffer.clear();
                    for (int k = start; k < temp; k++) {
                        for (int l = (temp - n); l < temp; l++) {
                            sBuffer.put(OPT[k][l]);
                        }
                    }
                    MPI.COMM_WORLD.iSend(sBuffer, (temp - start) * n, MPI.DOUBLE, rank - 1, tag);
                    // Because last processor will not receive anything and
                    // when a processor computes all the columns in its alloted rows it need not to receive anything
                    // but needs to send the last batch of columns
                    if (rank != processorCount - 1) {
                        if (j != N) {
                            MPI.COMM_WORLD.iRecv(rBuffer, (temp - start) * n, MPI.DOUBLE, rank + 1, tag);
                            //Put buffer values into OPT
                            for (int k = 0; k < (temp - start); k++) {
                                for (int m = 0; m < n; m++) {
                                    OPT[end + 1 + k][j + m] = rBuffer.get();
                                }
                            }
                            rBuffer.clear();
                        }
                    }
                    temp = temp + n;
                }
                if (j != N) {
                    for (int i = Math.min(j, end); i >= start; i--) {
                        getOPT(i, j, OPT);
                    }
                }

            }
        }
        MPI.Finalize();
    }

    /**
     * Main Function to execute ParallelOBST
     *
     * @param args
     * @throws MPIException
     * @throws IOException
     */
    public static void main(String[] args) throws MPIException, IOException {
        if (args.length == 3) {
            int N = Integer.parseInt(args[0]);
            int processorCount = Integer.parseInt(args[1]);
            String probabilitiesFile = args[2];
            ParallelOBST parallelOBST = new ParallelOBST(N, processorCount, probabilitiesFile);
            parallelOBST.executeParallel(args);

        } else if(args.length==2){
            int N = Integer.parseInt(args[0]);
            int processorCount = Integer.parseInt(args[1]);
            ParallelOBST parallelOBST = new ParallelOBST(N, processorCount);
            parallelOBST.executeParallel(args);
        }
        else{
            System.out.println("Usage: mpiexec --hostfile myHostfile java -cp mpi.jar ParallelOBST 500 8 Probabilities_500.txt \n" +
                    "          mpiexec --hostfile myHostfile java -cp mpi.jar ParallelOBST 500 8 ");
            return;
        }

    }
}
