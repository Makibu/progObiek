package solver;

public class Validator {
    
    public static ValidationResult validateSystem(LinearSystem system) {
        StringBuilder messages = new StringBuilder();
        boolean isValid = true;
        
        // Check dimensions
        if (system.getA().getRows() != system.getB().getSize()) {
            messages.append("Error: Matrix rows (").append(system.getA().getRows())
                   .append(") don't match vector size (").append(system.getB().getSize()).append(")\n");
            isValid = false;
        }
        
        // Check for NaN values
        for (int i = 0; i < system.getA().getRows(); i++) {
            for (int j = 0; j < system.getA().getCols(); j++) {
                if (Double.isNaN(system.getA().get(i, j))) {
                    messages.append("Error: NaN value found in matrix at position (").append(i).append(",").append(j).append(")\n");
                    isValid = false;
                }
            }
        }
        
        for (int i = 0; i < system.getB().getSize(); i++) {
            if (Double.isNaN(system.getB().get(i))) {
                messages.append("Error: NaN value found in vector at position ").append(i).append("\n");
                isValid = false;
            }
        }
        
        return new ValidationResult(isValid, messages.toString());
    }
    
    public static boolean isDiagonallyDominant(Matrix A) {
        int n = A.getRows();
        for (int i = 0; i < n; i++) {
            double diagonal = Math.abs(A.get(i, i));
            double rowSum = 0.0;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    rowSum += Math.abs(A.get(i, j));
                }
            }
            if (diagonal <= rowSum) {
                return false;
            }
        }
        return true;
    }
    
    public static double calculateDeterminant(Matrix A) {
        if (A.getRows() != A.getCols()) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant");
        }
        
        int n = A.getRows();
        Matrix temp = A.copy();
        double det = 1.0;
        
        for (int i = 0; i < n; i++) {
            // Find pivot
            int pivot = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(temp.get(j, i)) > Math.abs(temp.get(pivot, i))) {
                    pivot = j;
                }
            }
            
            if (pivot != i) {
                // Swap rows
                for (int j = 0; j < n; j++) {
                    double tmp = temp.get(i, j);
                    temp.set(i, j, temp.get(pivot, j));
                    temp.set(pivot, j, tmp);
                }
                det = -det;
            }
            
            if (Math.abs(temp.get(i, i)) < 1e-12) {
                return 0.0;
            }
            
            det *= temp.get(i, i);
            
            // Eliminate below
            for (int j = i + 1; j < n; j++) {
                double factor = temp.get(j, i) / temp.get(i, i);
                for (int k = i; k < n; k++) {
                    temp.set(j, k, temp.get(j, k) - factor * temp.get(i, k));
                }
            }
        }
        
        return det;
    }
    
    public static int calculateRank(Matrix A) {
        Matrix temp = A.copy();
        int rows = temp.getRows();
        int cols = temp.getCols();
        int rank = 0;
        
        for (int i = 0; i < rows; i++) {
            // Find pivot
            int pivotCol = -1;
            for (int j = 0; j < cols; j++) {
                if (Math.abs(temp.get(i, j)) > 1e-12) {
                    pivotCol = j;
                    break;
                }
            }
            
            if (pivotCol != -1) {
                rank++;
                // Eliminate below
                for (int k = i + 1; k < rows; k++) {
                    double factor = temp.get(k, pivotCol) / temp.get(i, pivotCol);
                    for (int j = pivotCol; j < cols; j++) {
                        temp.set(k, j, temp.get(k, j) - factor * temp.get(i, j));
                    }
                }
            }
        }
        
        return rank;
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}