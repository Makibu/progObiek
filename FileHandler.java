package solver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static LinearSystem readSystemFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        List<double[]> matrixRows = new ArrayList<>();
        List<Double> vectorElements = new ArrayList<>();

        // Read matrix dimensions
        line = reader.readLine();
        String[] dims = line.trim().split("\\s+");
        int rows = Integer.parseInt(dims[0]);
        int cols = Integer.parseInt(dims[1]);

        // Read matrix
        for (int i = 0; i < rows; i++) {
            line = reader.readLine();
            if (line == null)
                throw new IOException("Unexpected end of file");
            String[] values = line.trim().split("\\s+");
            double[] row = new double[cols];
            for (int j = 0; j < cols; j++) {
                row[j] = Double.parseDouble(values[j]);
            }
            matrixRows.add(row);
        }

        // Read vector
        for (int i = 0; i < rows; i++) {
            line = reader.readLine();
            if (line == null)
                throw new IOException("Unexpected end of file");
            vectorElements.add(Double.parseDouble(line.trim()));
        }

        reader.close();

        // Create matrix and vector
        Matrix A = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                A.set(i, j, matrixRows.get(i)[j]);
            }
        }

        double[] bArray = new double[rows];
        for (int i = 0; i < rows; i++) {
            bArray[i] = vectorElements.get(i);
        }
        Vector b = new Vector(bArray);

        return new LinearSystem(A, b);
    }

    public static void writeResultToFile(String filename, Result result, LinearSystem system) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("LINEAR EQUATIONS SOLVER - RESULTS\n");
        writer.write("================================\n\n");

        writer.write("ORIGINAL SYSTEM:\n");
        writer.write(system.toString());
        writer.write("\n");

        writer.write("SOLUTION:\n");
        writer.write(result.toString());
        writer.write("\n");

        if (result.getSolution() != null) {
            writer.write("VERIFICATION (A*x should equal b):\n");
            // Naprawione: używamy metody pomocniczej do konwersji
            Matrix AxMatrix = system.getA().multiply(result.getSolution());
            Vector Ax = matrixToVector(AxMatrix);
            writer.write("A*x = " + Ax.toString() + "\n");
            writer.write("b   = " + system.getB().toString() + "\n");
        }

        writer.close();
    }

    // Dodana pomocnicza metoda do konwersji Matrix na Vector
    private static Vector matrixToVector(Matrix matrix) {
        if (matrix.getCols() != 1) {
            throw new IllegalArgumentException("Matrix must have exactly one column to convert to Vector");
        }
        Vector vector = new Vector(matrix.getRows());
        for (int i = 0; i < matrix.getRows(); i++) {
            vector.set(i, matrix.get(i, 0));
        }
        return vector;
    }

    public static void writeMatrixToFile(String filename, Matrix matrix) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(matrix.toCompactString());
        writer.close();
    }
}