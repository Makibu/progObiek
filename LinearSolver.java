package solver;

public abstract class LinearSolver {
    protected int iterations;
    protected long computationTime;
    protected int maxIterations = 1000;
    protected double tolerance = 1e-10;

    public abstract Result solve(LinearSystem system);

    public int getIterations() {
        return iterations;
    }

    public long getComputationTime() {
        return computationTime;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    protected void validateSystem(LinearSystem system) {
        if (system.getA().getRows() != system.getA().getCols()) {
            throw new IllegalArgumentException("Matrix must be square for this solver");
        }
    }

    protected double calculateResidual(LinearSystem system, Vector solution) {
        // Naprawione: konwertujemy wynik mnożenia Matrix na Vector
        Matrix AxMatrix = system.getA().multiply(solution);
        Vector Ax = matrixToVector(AxMatrix);
        Vector residual = Ax.subtract(system.getB());
        return residual.norm();
    }

    // Dodana pomocnicza metoda do konwersji Matrix na Vector
    private Vector matrixToVector(Matrix matrix) {
        if (matrix.getCols() != 1) {
            throw new IllegalArgumentException("Matrix must have exactly one column to convert to Vector");
        }
        Vector vector = new Vector(matrix.getRows());
        for (int i = 0; i < matrix.getRows(); i++) {
            vector.set(i, matrix.get(i, 0));
        }
        return vector;
    }
}