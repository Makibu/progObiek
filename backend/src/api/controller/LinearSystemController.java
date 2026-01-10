package api.controller;

import api.core.LinearSystem;
import api.core.Result;
import api.core.Vector;
import api.core.Matrix;
import api.core.solver.GaussSolver;
import api.core.solver.JacobiSolver;
import api.core.solver.GaussSeidelSolver;

import org.json.JSONObject;

public class LinearSystemController {

    private final GaussSolver gaussSolver = new GaussSolver();
    private final JacobiSolver jacobiSolver = new JacobiSolver();
    private final GaussSeidelSolver gaussSeidelSolver = new GaussSeidelSolver();

    public Result solveGauss(LinearSystem system) {
        return gaussSolver.solve(system);
    }

    public Result solveJacobi(LinearSystem system) {
        return jacobiSolver.solve(system);
    }

    public Result solveGaussSeidel(LinearSystem system) {
        return gaussSeidelSolver.solve(system);
    }

    public LinearSystem parseSystemFromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        var aJson = json.getJSONArray("A");
        int rows = aJson.length();
        int cols = aJson.getJSONArray(0).length();
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            var row = aJson.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                data[i][j] = row.getDouble(j);
            }
        }
        Matrix A = new Matrix(data);

        var bJson = json.getJSONArray("b");
        double[] bArr = new double[bJson.length()];
        for (int i = 0; i < bJson.length(); i++) {
            bArr[i] = bJson.getDouble(i);
        }
        Vector b = new Vector(bArr);

        return new LinearSystem(A, b);
    }

    public JSONObject resultToJson(Result result) {
        JSONObject json = new JSONObject();

        // --- solution ---
        if (result.getSolution() != null) {
            double[] solArray = result.getSolution().toArray();

            boolean invalid = false;
            for (double v : solArray) {
                if (Double.isNaN(v) || Double.isInfinite(v)) {
                    invalid = true;
                    break;
                }
            }

            if (!invalid) {
                json.put("solution", solArray);
            } else {
                json.put("solution", JSONObject.NULL);
            }
        } else {
            json.put("solution", JSONObject.NULL);
        }

        json.put("message", result.getMessage());

        // --- residual ---
        double residual = result.getResidual();
        json.put("residual", Double.isFinite(residual) ? residual : 0.0);

        // --- iterations ---
        json.put("iterations", result.getIterations());

        // --- computationTime ---
        double time = result.getComputationTime();
        json.put("computationTime", Double.isFinite(time) ? time : 0.0);

        // --- determinant ---
        double det = result.getDeterminant();
        if (Double.isFinite(det) && det != 0) {
            json.put("determinant", det);
        }

        // --- rank ---
        int rank = result.getRank();
        if (rank != 0) {
            json.put("rank", rank);
        }

        return json;
    }

}
