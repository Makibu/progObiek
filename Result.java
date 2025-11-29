package solver;

public class Result {
    private Vector solution;
    private String message;
    private int iterations;
    private long computationTime;
    private double residual;
    private double determinant;
    private int rank;
    
    public Result(Vector solution, String message, int iterations, long computationTime) {
        this(solution, message, iterations, computationTime, 0.0);
    }
    
    public Result(Vector solution, String message, int iterations, long computationTime, double residual) {
        this.solution = solution;
        this.message = message;
        this.iterations = iterations;
        this.computationTime = computationTime;
        this.residual = residual;
    }
    
    // Getters
    public Vector getSolution() { return solution; }
    public String getMessage() { return message; }
    public int getIterations() { return iterations; }
    public long getComputationTime() { return computationTime; }
    public double getResidual() { return residual; }
    public double getDeterminant() { return determinant; }
    public int getRank() { return rank; }
    
    // Setters
    public void setDeterminant(double determinant) { this.determinant = determinant; }
    public void setRank(int rank) { this.rank = rank; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Result: ").append(message).append("\n");
        if (solution != null) {
            sb.append("Solution: ").append(solution.toString()).append("\n");
            sb.append(String.format("Residual norm: %.6e\n", residual));
        }
        sb.append("Iterations: ").append(iterations).append("\n");
        sb.append("Computation time: ").append(computationTime).append(" ms\n");
        if (determinant != 0) {
            sb.append(String.format("Determinant: %.6f\n", determinant));
        }
        if (rank != 0) {
            sb.append("Matrix rank: ").append(rank).append("\n");
        }
        return sb.toString();
    }
}