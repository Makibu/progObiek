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

        if (result.getSolution() != null) {
            json.put("solution", result.getSolution().toArray());
        } else {
            json.put("solution", JSONObject.NULL);
        }

        json.put("message", result.getMessage() != null ? result.getMessage() : "Matrix has no solution");

        json.put("iterations", result.getIterations());
        json.put("computationTime", result.getComputationTime());
        json.put("residual", result.getResidual());

        if (result.getDeterminant() != 0) json.put("determinant", result.getDeterminant());
        if (result.getRank() != 0) json.put("rank", result.getRank());

        return json;
    }
}
