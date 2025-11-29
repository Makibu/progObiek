package solver;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

public class LinearSolverApp extends JFrame {
    private LinearSystem currentSystem;
    private Result currentResult;
    private JTextArea outputArea;
    private JTextField matrixInput;
    private JTextField vectorInput;
    
    public LinearSolverApp() {
        setTitle("Linear Equations Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        matrixInput = new JTextField();
        vectorInput = new JTextField();
        
        inputPanel.add(new JLabel("Matrix (comma separated, rows separated by semicolon):"));
        inputPanel.add(matrixInput);
        inputPanel.add(new JLabel("Vector (comma separated):"));
        inputPanel.add(vectorInput);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton loadFileBtn = new JButton("Load from File");
        JButton manualInputBtn = new JButton("Manual Input");
        JButton gaussBtn = new JButton("Solve with Gauss");
        JButton jacobiBtn = new JButton("Solve with Jacobi");
        JButton gaussSeidelBtn = new JButton("Solve with Gauss-Seidel");
        JButton analyzeBtn = new JButton("Analyze System");
        JButton saveBtn = new JButton("Save Results");
        
        buttonPanel.add(loadFileBtn);
        buttonPanel.add(manualInputBtn);
        buttonPanel.add(gaussBtn);
        buttonPanel.add(jacobiBtn);
        buttonPanel.add(gaussSeidelBtn);
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(saveBtn);
        
        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add action listeners
        loadFileBtn.addActionListener(new LoadFileListener());
        manualInputBtn.addActionListener(new ManualInputListener());
        gaussBtn.addActionListener(new GaussListener());
        jacobiBtn.addActionListener(new JacobiListener());
        gaussSeidelBtn.addActionListener(new GaussSeidelListener());
        analyzeBtn.addActionListener(new AnalyzeListener());
        saveBtn.addActionListener(new SaveListener());
    }
    
    private class LoadFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(LinearSolverApp.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    currentSystem = FileHandler.readSystemFromFile(fileChooser.getSelectedFile().getPath());
                    outputArea.setText("System loaded successfully:\n" + currentSystem.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(LinearSolverApp.this, "Error loading file: " + ex.getMessage());
                }
            }
        }
    }
    
    private class ManualInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String matrixText = matrixInput.getText();
                String vectorText = vectorInput.getText();
                
                // Parse matrix
                String[] rows = matrixText.split(";");
                int numRows = rows.length;
                int numCols = rows[0].split(",").length;
                
                Matrix A = new Matrix(numRows, numCols);
                for (int i = 0; i < numRows; i++) {
                    String[] values = rows[i].split(",");
                    for (int j = 0; j < numCols; j++) {
                        A.set(i, j, Double.parseDouble(values[j].trim()));
                    }
                }
                
                // Parse vector
                String[] vectorValues = vectorText.split(",");
                Vector b = new Vector(vectorValues.length);
                for (int i = 0; i < vectorValues.length; i++) {
                    b.set(i, Double.parseDouble(vectorValues[i].trim()));
                }
                
                currentSystem = new LinearSystem(A, b);
                
                // Validate
                Validator.ValidationResult validation = Validator.validateSystem(currentSystem);
                if (validation.isValid()) {
                    outputArea.setText("System created successfully:\n" + currentSystem.toString());
                } else {
                    outputArea.setText("System created with warnings:\n" + validation.getMessage() + "\n" + currentSystem.toString());
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LinearSolverApp.this, "Error parsing input: " + ex.getMessage());
            }
        }
    }
    
    private class GaussListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentSystem == null) {
                JOptionPane.showMessageDialog(LinearSolverApp.this, "Please load or input a system first");
                return;
            }
            
            GaussSolver solver = new GaussSolver();
            // Ask for pivoting
            int response = JOptionPane.showConfirmDialog(LinearSolverApp.this, "Use partial pivoting?", "Gauss Method", JOptionPane.YES_NO_OPTION);
            solver.setPartialPivoting(response == JOptionPane.YES_OPTION);
            
            currentResult = solver.solve(currentSystem);
            outputArea.setText(currentResult.toString());
        }
    }
    
    private class JacobiListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentSystem == null) {
                JOptionPane.showMessageDialog(LinearSolverApp.this, "Please load or input a system first");
                return;
            }
            
            // Check diagonal dominance
            if (!Validator.isDiagonallyDominant(currentSystem.getA())) {
                int response = JOptionPane.showConfirmDialog(LinearSolverApp.this, 
                    "Matrix is not diagonally dominant. Jacobi method may not converge. Continue anyway?",
                    "Warning", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            JacobiSolver solver = new JacobiSolver();
            configureIterativeSolver(solver);
            currentResult = solver.solve(currentSystem);
            outputArea.setText(currentResult.toString());
        }
    }
    
    private class GaussSeidelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentSystem == null) {
                JOptionPane.showMessageDialog(LinearSolverApp.this, "Please load or input a system first");
                return;
            }
            
            // Check diagonal dominance
            if (!Validator.isDiagonallyDominant(currentSystem.getA())) {
                int response = JOptionPane.showConfirmDialog(LinearSolverApp.this, 
                    "Matrix is not diagonally dominant. Gauss-Seidel method may not converge. Continue anyway?",
                    "Warning", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            GaussSeidelSolver solver = new GaussSeidelSolver();
            configureIterativeSolver(solver);
            currentResult = solver.solve(currentSystem);
            outputArea.setText(currentResult.toString());
        }
    }
    
    private void configureIterativeSolver(LinearSolver solver) {
        String maxIter = JOptionPane.showInputDialog("Maximum iterations (default: 1000):", "1000");
        String tol = JOptionPane.showInputDialog("Tolerance (default: 1e-10):", "1e-10");
        
        try {
            if (maxIter != null && !maxIter.trim().isEmpty()) {
                solver.setMaxIterations(Integer.parseInt(maxIter));
            }
            if (tol != null && !tol.trim().isEmpty()) {
                solver.setTolerance(Double.parseDouble(tol));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format. Using defaults.");
        }
    }
    
    private class AnalyzeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentSystem == null) {
                JOptionPane.showMessageDialog(LinearSolverApp.this, "Please load or input a system first");
                return;
            }
            
            StringBuilder analysis = new StringBuilder();
            analysis.append("SYSTEM ANALYSIS\n");
            analysis.append("===============\n");
            analysis.append("Matrix size: ").append(currentSystem.getA().getRows()).append("x").append(currentSystem.getA().getCols()).append("\n");
            
            if (currentSystem.getA().getRows() == currentSystem.getA().getCols()) {
                double det = Validator.calculateDeterminant(currentSystem.getA());
                analysis.append(String.format("Determinant: %.6f\n", det));
                analysis.append("Diagonally dominant: ").append(Validator.isDiagonallyDominant(currentSystem.getA())).append("\n");
            }
            
            int rank = Validator.calculateRank(currentSystem.getA());
            analysis.append("Matrix rank: ").append(rank).append("\n");
            
            outputArea.setText(analysis.toString());
        }
    }
    
    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentResult == null) {
                JOptionPane.showMessageDialog(LinearSolverApp.this, "No results to save");
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(LinearSolverApp.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    FileHandler.writeResultToFile(fileChooser.getSelectedFile().getPath(), currentResult, currentSystem);
                    JOptionPane.showMessageDialog(LinearSolverApp.this, "Results saved successfully");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(LinearSolverApp.this, "Error saving file: " + ex.getMessage());
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LinearSolverApp().setVisible(true);
        });
    }
}