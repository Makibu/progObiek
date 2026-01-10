package api.core.solver;

import api.core.LinearSystem;
import api.core.Matrix;
import api.core.Result;
import api.core.Vector;

public class GaussSolver extends LinearSolver {
    private boolean partialPivoting = true;

    public void setPartialPivoting(boolean partialPivoting) {
        this.partialPivoting = partialPivoting; // turn on/off pivoting
    }

    @Override
    public Result solve(LinearSystem system) {
        long startTime = System.nanoTime();
        validateSystem(system); // check if system is ok

        int n = system.getSize();
        Matrix A = system.getA().copy();
        Vector b = Vector.fromArray(system.getB().toArray());

        // go row by row to make upper triangle
        for (int i = 0; i < n; i++) {
            if (partialPivoting) {
                partialPivot(A, b, i); // swap rows if needed
            }

            if (Math.abs(A.get(i, i)) < tolerance) {
                computationTime = (System.nanoTime() - startTime) / 1000000;
                return new Result(null, "Matrix is singular or nearly singular", 0, computationTime);
            }

            // remove values below pivot
            for (int j = i + 1; j < n; j++) {
                double factor = A.get(j, i) / A.get(i, i);
                for (int k = i; k < n; k++) {
                    A.set(j, k, A.get(j, k) - factor * A.get(i, k));
                }
                b.set(j, b.get(j) - factor * b.get(i));
            }
        }

        // calculate solution from top
        Vector solution = new Vector(n);
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += A.get(i, j) * solution.get(j);
            }
            solution.set(i, (b.get(i) - sum) / A.get(i, i));
        }

        computationTime = (System.nanoTime() - startTime) / 1000000;
        double residual = calculateResidual(system, solution);
        return new Result(solution, "Solution found", iterations, computationTime, residual);
    }

    private void partialPivot(Matrix A, Vector b, int row) {
        int n = A.getRows();
        int maxRow = row;
        double maxVal = Math.abs(A.get(row, row));

        // find row with biggest pivot
        for (int i = row + 1; i < n; i++) {
            if (Math.abs(A.get(i, row)) > maxVal) {
                maxVal = Math.abs(A.get(i, row));
                maxRow = i;
            }
        }

        if (maxRow != row) {
            // swap rows in matrix
            for (int j = 0; j < n; j++) {
                double temp = A.get(row, j);
                A.set(row, j, A.get(maxRow, j));
                A.set(maxRow, j, temp);
            }
            // swap rows in vector
            double temp = b.get(row);
            b.set(row, b.get(maxRow));
            b.set(maxRow, temp);
        }
    }
}
