package solver;

public class GaussSeidelSolver extends LinearSolver {
    
    @Override
    public Result solve(LinearSystem system) {
        long startTime = System.nanoTime();
        validateSystem(system);
        
        int n = system.getSize();
        Vector x = new Vector(n); // Initial guess (zeros)
        
        iterations = 0;
        double error;
        
        do {
            error = 0.0;
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum += system.getA().get(i, j) * x.get(j);
                    }
                }
                double xNew = (system.getB().get(i) - sum) / system.getA().get(i, i);
                error += Math.abs(xNew - x.get(i));
                x.set(i, xNew);
            }
            
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