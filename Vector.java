package solver;

public class Vector extends Matrix {

    public Vector(int size) {
        super(size, 1);
    }

    public Vector(double[] data) {
        super(data.length, 1);
        for (int i = 0; i < data.length; i++) {
            set(i, 0, data[i]);
        }
    }

    public int getSize() {
        return getRows();
    }

    public double get(int i) {
        return super.get(i, 0);
    }

    public void set(int i, double value) {
        super.set(i, 0, value);
    }

    public double dotProduct(Vector other) {
        if (this.getSize() != other.getSize()) {
            throw new IllegalArgumentException("Vector sizes don't match");
        }
        double sum = 0;
        for (int i = 0; i < getSize(); i++) {
            sum += this.get(i) * other.get(i);
        }
        return sum;
    }

    public double norm() {
        return Math.sqrt(dotProduct(this));
    }

    // Poprawiona metoda add - nie używaj super.add()
    public Vector add(Vector other) {
        if (this.getSize() != other.getSize()) {
            throw new IllegalArgumentException("Vector sizes don't match for addition");
        }
        Vector result = new Vector(getSize());
        for (int i = 0; i < getSize(); i++) {
            result.set(i, this.get(i) + other.get(i));
        }
        return result;
    }

    public Vector subtract(Vector other) {
        if (this.getSize() != other.getSize()) {
            throw new IllegalArgumentException("Vector sizes don't match for subtraction");
        }
        Vector result = new Vector(getSize());
        for (int i = 0; i < getSize(); i++) {
            result.set(i, this.get(i) - other.get(i));
        }
        return result;
    }

    public Vector multiply(double scalar) {
        Vector result = new Vector(getSize());
        for (int i = 0; i < getSize(); i++) {
            result.set(i, this.get(i) * scalar);
        }
        return result;
    }

    // Publiczna metoda do konwersji na tablicę
    public double[] toArray() {
        double[] array = new double[getSize()];
        for (int i = 0; i < getSize(); i++) {
            array[i] = get(i);
        }
        return array;
    }

    // Statyczna metoda do tworzenia Vector z tablicy
    public static Vector fromArray(double[] data) {
        return new Vector(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < getSize(); i++) {
            sb.append(String.format("%10.6f ", get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
}