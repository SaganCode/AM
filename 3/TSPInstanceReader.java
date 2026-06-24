import java.io.*;
import java.util.*;

/**
 * Klasa do czytania plików w formacie TSP
 */
public class TSPInstanceReader {
    
    public static TSPProblem readTSPFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String problemName = "";
        int dimension = 0;
        String edgeWeightType = "EUC_2D";
        List<double[]> coordinates = new ArrayList<>();
        boolean readingCoordinates = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("EOF")) {
                break;
            }

            if (line.startsWith("NAME:")) {
                problemName = line.substring(5).trim();
            } else if (line.startsWith("DIMENSION:")) {
                dimension = Integer.parseInt(line.substring(10).trim());
            } else if (line.startsWith("EDGE_WEIGHT_TYPE:")) {
                edgeWeightType = line.substring(17).trim();
            } else if (line.startsWith("NODE_COORD_SECTION")) {
                readingCoordinates = true;
                continue;
            }

            if (readingCoordinates && !line.isEmpty()) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    try {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        coordinates.add(new double[]{x, y});
                    } catch (NumberFormatException e) {
                        // Ignoruj niepoprawne linie
                    }
                }
            }
        }
        reader.close();

        // Oblicz macierz odległości
        double[][] distanceMatrix = calculateDistanceMatrix(coordinates, edgeWeightType);
        double[][] coordinateArray = coordinates.toArray(new double[coordinates.size()][]);
        return new TSPProblem(distanceMatrix, coordinateArray, problemName);
    }

    private static double[][] calculateDistanceMatrix(List<double[]> coordinates, String edgeWeightType) {
        int n = coordinates.size();
        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    double[] coord1 = coordinates.get(i);
                    double[] coord2 = coordinates.get(j);
                    matrix[i][j] = calculateDistance(coord1, coord2, edgeWeightType);
                }
            }
        }
        return matrix;
    }

    private static double calculateDistance(double[] coord1, double[] coord2, String type) {
        double dx = coord1[0] - coord2[0];
        double dy = coord1[1] - coord2[1];

        if ("EUC_2D".equals(type)) {
            return Math.sqrt(dx * dx + dy * dy);
        } else if ("MANHATTAN".equals(type)) {
            return Math.abs(dx) + Math.abs(dy);
        } else {
            return Math.sqrt(dx * dx + dy * dy); // default
        }
    }
}
