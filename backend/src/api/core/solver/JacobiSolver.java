package api.core.solver;

import api.core.LinearSystem;
import api.core.Result;
import api.core.Validator;
import api.core.Vector;

public class JacobiSolver extends LinearSolver {

    // Counter for consecutive error increases
    private int consecutiveIncreases = 0;
    private static final int MAX_CONSECUTIVE_INCREASES = 10;

    @Override
    public Result solve(LinearSystem system) {
        long startTime = System.nanoTime();
        validateSystem(system);

        if (!Validator.isDiagonallyDominant(system.getA())) {
            long computationTime = (long) ((System.nanoTime() - startTime) / 1_000_000.0);
            return new Result(null, "Matrix is not diagonally dominant – method may not converge", 0, computationTime);
        }

        int n = system.getSize();
        Vector x = new Vector(n); // initial guess = 0
        Vector xNew = new Vector(n);

        iterations = 0;
        consecutiveIncreases = 0;
        double error;
        double previousError = Double.MAX_VALUE;

        do {
            // Jacobi iteration
            for (int i = 0; i < n; i++) {
                double diag = system.getA().get(i, i);
                if (Math.abs(diag) < tolerance) { // dzielenie przez 0
                    computationTime = (System.nanoTime() - startTime) / 1_000_000;
                    return new Result(null, "Division by zero on diagonal", iterations, computationTime);
                }

                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j != i) sum += system.getA().get(i, j) * x.get(j);
                }

                double value = (system.getB().get(i) - sum) / diag;
                if (Double.isNaN(value) || Double.isInfinite(value)) { // divergenacja
                    computationTime = (System.nanoTime() - startTime) / 1_000_000;
                    return new Result(null, "NaN or Infinity encountered", iterations, computationTime);

                }

                xNew.set(i, value);
            }

            error = xNew.subtract(x).norm();

            // Check divergence
            if (error > previousError) {
                consecutiveIncreases++;
                if (consecutiveIncreases >= MAX_CONSECUTIVE_INCREASES) {
                    computationTime = (System.nanoTime() - startTime) / 1_000_000;
                    return new Result(null, "Diverged", iterations, computationTime);
                }
            } else {
                consecutiveIncreases = 0;
            }

            previousError = error;
            x = Vector.fromArray(xNew.toArray());
            iterations++;

            if (iterations > maxIterations) {
                computationTime = (System.nanoTime() - startTime) / 1_000_000;
                return new Result(null, "Maximum iterations reached", iterations, computationTime);
            }
        } while (error > tolerance);

        // konwergencja
        computationTime = (System.nanoTime() - startTime) / 1_000_000;
        double residual = calculateResidual(system, x);
        return new Result(x, "Converged", iterations, computationTime, residual);
    }


}