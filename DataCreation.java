/**
 * Author={"Amrutha Varshini Mandalreddy}
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataCreation {
    public int N;

    DataCreation(int N) {
        this.N = N;
    }

    /**
     * This function generates Probabilities and saves into the file
     *
     * @return
     * @throws IOException
     */
    public Double[] generateProbabilities(String fileName) throws IOException {
        Double[] probabilities = new Double[N];
        Random random = new Random();
        Double min = 0.0;
        Double max = 1.0;
        for (int i = 0; i < N; i++) {
            Double current = min + random.nextFloat() * (max - min);
            probabilities[i] = current;
            max = max - current;
        }
        java.io.File file = new File(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < probabilities.length; i++) {
            bufferedWriter.write(probabilities[i] + " ");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        return probabilities;
    }


    /**
     * Main Function to create data
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            int N = Integer.parseInt(args[0]);
            String fileName = args[1];
            DataCreation data = new DataCreation(N);
            data.generateProbabilities(fileName);
        } else {
            System.out.println("Usage: DataCreation 500 probabilities_500.txt");
        }


    }
}
