package solver;

public class GaussSolver extends LinearSolver {
    private boolean partialPivoting = true;

    public void setPartialPivoting(boolean partialPivoting) {
        this.partialPivoting = partialPivoting;
    }

    @Override
    public Result solve(LinearSystem system) {
        long startTime = System.nanoTime();
        validateSystem(system);

        int n = system.getSize();
        Matrix A = system.getA().copy();
        Vector b = Vector.fromArray(system.getB().toArray());

        // Forward elimination
        for (int i = 0; i < n; i++) {
            if (partialPivoting) {
                partialPivot(A, b, i);
            }

            // Check for zero pivot
            if (Math.abs(A.get(i, i)) < tolerance) {
                computationTime = (System.nanoTime() - startTime) / 1000000;
                return new Result(null, "Matrix is singular or nearly singular", 0, computationTime);
            }

            // Eliminate below
            for (int j = i + 1; j < n; j++) {
                double factor = A.get(j, i) / A.get(i, i);
                for (int k = i; k < n; k++) {
                    A.set(j, k, A.get(j, k) - factor * A.get(i, k));
                }
                b.set(j, b.get(j) - factor * b.get(i));
            }
        }

        // Back substitution
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

        for (int i = row + 1; i < n; i++) {
            if (Math.abs(A.get(i, row)) > maxVal) {
                maxVal = Math.abs(A.get(i, row));
                maxRow = i;
            }
        }

        if (maxRow != row) {
            // Swap rows in A
            for (int j = 0; j < n; j++) {
                double temp = A.get(row, j);
                A.set(row, j, A.get(maxRow, j));
                A.set(maxRow, j, temp);
            }
            // Swap rows in b
            double temp = b.get(row);
            b.set(row, b.get(maxRow));
            b.set(maxRow, temp);
        }
    }
}