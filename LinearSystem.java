package solver;

public class LinearSystem {
    private Matrix A;
    private Vector b;
    
    public LinearSystem(Matrix A, Vector b) {
        if (A.getRows() != b.getSize()) {
            throw new IllegalArgumentException("Matrix A and vector b have incompatible dimensions");
        }
        this.A = A;
        this.b = b;
    }
    
    public Matrix getA() { return A; }
    public Vector getB() { return b; }
    public int getSize() { return A.getRows(); }
    
    public void setA(Matrix A) { this.A = A; }
    public void setB(Vector b) { this.b = b; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("System of ").append(getSize()).append(" equations:\n");
        for (int i = 0; i < getSize(); i++) {
            sb.append("Eq ").append(i + 1).append(": ");
            for (int j = 0; j < A.getCols(); j++) {
                sb.append(String.format("%6.2f*x%d", A.get(i, j), j + 1));
                if (j < A.getCols() - 1) sb.append(" + ");
            }
            sb.append(String.format(" = %6.2f\n", b.get(i)));
        }
        return sb.toString();
    }
}