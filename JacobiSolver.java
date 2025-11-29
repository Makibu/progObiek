package solver;

public class JacobiSolver extends LinearSolver {

    @Override
    public Result solve(LinearSystem system) {
        long startTime = System.nanoTime();
        validateSystem(system);

        int n = system.getSize();
        Vector x = new Vector(n); // Initial guess (zeros)
        Vector xNew = new Vector(n);

        iterations = 0;
        double error;

        do {
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum += system.getA().get(i, j) * x.get(j);
                    }
                }
                xNew.set(i, (system.getB().get(i) - sum) / system.getA().get(i, i));
            }

            error = xNew.subtract(x).norm();
            x = Vector.fromArray(xNew.toArray());
            iterations++;

            if (iterations > maxIterations) {
                computationTime = (System.nanoTime() - startTime) / 1000000;
                return new Result(x, "Maximum iterations reached", iterations, computationTime, error);
            }
        } while (error > tolerance);

        computationTime = (System.nanoTime() - startTime) / 1000000;
        double residual = calculateResidual(system, x);
        return new Result(x, "Converged", iterations, computationTime, residual);
    }
}