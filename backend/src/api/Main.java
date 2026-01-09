package api;

import api.controller.LinearSystemController;
import api.core.LinearSystem;
import api.core.Result;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        LinearSystemController controller = new LinearSystemController();

        server.createContext("/solve", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject json = new JSONObject(body);

                    String solver = json.optString("solver", "");
                    LinearSystem system = controller.parseSystemFromJson(body);

                    Result result;
                    switch (solver) {
                        case "gauss":
                            result = controller.solveGauss(system);
                            break;
                        case "jacobi":
                            result = controller.solveJacobi(system);
                            break;
                        case "gauss-seidel":
                            result = controller.solveGaussSeidel(system);
                            break;
                        default:
                            result = new Result(null, "Unknown solver: " + solver, 0, 0);
                    }

                    JSONObject respJson = controller.resultToJson(result);
                    byte[] respBytes = respJson.toString().getBytes(StandardCharsets.UTF_8);

                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respBytes.length);
                    exchange.getResponseBody().write(respBytes);
                    exchange.close();

                } catch (Exception e) {
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("solution", JSONObject.NULL);
                    errorJson.put("message", "Server error: " + e.getMessage());
                    errorJson.put("iterations", 0);
                    errorJson.put("computationTime", 0);
                    byte[] respBytes = errorJson.toString().getBytes(StandardCharsets.UTF_8);

                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respBytes.length);
                    exchange.getResponseBody().write(respBytes);
                    exchange.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Serwer wystartował na http://localhost:8080");
    }
}
