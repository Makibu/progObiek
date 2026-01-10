package api.core.solver;

import api.core.LinearSystem;
import api.core.Result;
import api.core.Validator;
import api.core.Vector;

public class GaussSeidelSolver extends LinearSolver {

    @Override
    public Result solve(LinearSystem system) {
        long startTime = System.nanoTime();
        validateSystem(system);

        int n = system.getSize();
        Vector x = new Vector(n);

        if (!Validator.isDiagonallyDominant(system.getA())) {
            long computationTime = (long) ((System.nanoTime() - startTime) / 1_000_000.0);
            return new Result(null, "Matrix is not diagonally dominant – method may not converge", 0, computationTime);
        }

        iterations = 0;

        while (iterations < maxIterations) {
            double error = 0.0;

            for (int i = 0; i < n; i++) {
                double diag = system.getA().get(i, i);
                if (Math.abs(diag) < tolerance) {
                    computationTime = (System.nanoTime() - startTime) / 1_000_000;
                    return new Result(null, "Diagonal element zero – cannot solve", iterations, computationTime);
                }

                double sum = 0.0;
                for (int j = 0; j < n; j++) if (j != i) sum += system.getA().get(i, j) * x.get(j);

                double xNew = (system.getB().get(i) - sum) / diag;
                if (Double.isNaN(xNew) || Double.isInfinite(xNew)) {
                    computationTime = (System.nanoTime() - startTime) / 1_000_000;
                    return new Result(null, "NaN or Infinity encountered", iterations, computationTime);
                }

                error += Math.abs(xNew - x.get(i));
                x.set(i, xNew);
            }

            iterations++;

            if (error < tolerance) {
                computationTime = (System.nanoTime() - startTime) / 1_000_000;
                double residual = calculateResidual(system, x);
                return new Result(x, "Converged", iterations, computationTime, residual);
            }
        }

        computationTime = (System.nanoTime() - startTime) / 1_000_000;
        return new Result(null, "Maximum iterations reached – did not converge", iterations, computationTime);
    }



}