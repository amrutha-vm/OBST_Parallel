/**
 * Author = "Amrutha Varshini Mandalreddy"
 */

import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Class for serial Computation of Optimal Binary Search Tree
 */
public class SerialOBST {

    public int n;
    public Double[] probabilities;

    SerialOBST(int n, String fileName) throws IOException {
        this.n = n;
        this.probabilities = loadProbabilities(n, fileName);
    }

    SerialOBST(int n) throws IOException {
        this.n = n;
        this.probabilities = generateProbabilities(n);
    }

    /**
     * Method to load n probabilities from file
     *
     * @param n
     * @param fileName
     * @return
     * @throws IOException
     */
    public Double[] loadProbabilities(int n, String fileName) throws IOException {
        probabilities = new Double[n];
        java.io.File file = new File(fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = "";
        while ((s = bufferedReader.readLine()) != null) {
            String[] strings = s.split(" ");
            for (int i = 0; i < strings.length; i++) {
                probabilities[i] = Double.parseDouble(strings[i]);
            }
        }
        return probabilities;
    }

    /**
     * Generate probabilities of length N if not loading from a file
     * @param N
     * @return
     */
    public static Double[]  generateProbabilities(int N) {
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
     * Calculate Optimal Cost matrix
     *
     * @param i
     * @param j
     * @param OPT
     * @return
     */
    public Double getOPT(int i, int j, Double[][] OPT) {
        if (i < 0 || j < 0 || i >= n || j >= n || j < i) {
            return 0.0;
        }
        if (OPT[i][j] == null) {
            Double min = -1.0;
            Double probabilitySUM = probabilitySUM(i, j);
            for (int k = i; k <= j; k++) {
                Double temp = getOPT(i, k - 1, OPT) + getOPT(k + 1, j, OPT) + probabilitySUM;
                if (min == -1.0) {
                    min = temp;
                } else if (temp < min) {
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
     * Method to call getOPT
     *
     * @return
     */
    public Double getOptimalBinarySearchTree() {
        Double[][] OPT = new Double[n][n];
        return getOPT(0, n - 1, OPT);
    }

    /**
     * Print 2-Dimensional Array to debug
     *
     * @param OPT
     */
    public void printString(Double[][] OPT) {
        for (int i = 0; i < OPT.length; i++) {
            System.out.println(Arrays.toString(OPT[i]));
        }
    }

    /**
     * Main Method for serialOBST
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            int n = Integer.parseInt(args[0]);
            String fileName = args[1];
            SerialOBST serialOBST = new SerialOBST(n, fileName);
            System.out.println(serialOBST.getOptimalBinarySearchTree());
        }
        else if (args.length == 1) {
            int n = Integer.parseInt(args[0]);
            SerialOBST serialOBST = new SerialOBST(n);
            System.out.println(serialOBST.getOptimalBinarySearchTree());
        }
        else {
            System.out.println("Usage: SerialBOST 500 probabilities_500.txt \n" +
                    "Usage: SerialBOST 500 ");
        }
    }
}
