/**
 * Klasa reprezentująca problem TSP (Traveling Salesman Problem)
 */
public class TSPProblem {
    private double[][] distanceMatrix;
    private double[][] coordinates;
    private int numberOfCities;
    private String problemName;

    public TSPProblem(double[][] distanceMatrix, double[][] coordinates, String problemName) {
        this.distanceMatrix = distanceMatrix;
        this.coordinates = coordinates;
        this.numberOfCities = distanceMatrix.length;
        this.problemName = problemName;
    }

    /**
     * Oblicza dystans całej trasy
     */
    public double calculateTourLength(int[] tour) {
        double totalDistance = 0;
        for (int i = 0; i < numberOfCities; i++) {
            int from = tour[i];
            int to = tour[(i + 1) % numberOfCities];
            totalDistance += distanceMatrix[from][to];
        }
        return totalDistance;
    }

    /**
     * Oblicza dystans między dwoma miastami
     */
    public double getDistance(int city1, int city2) {
        return distanceMatrix[city1][city2];
    }

    public int getNumberOfCities() {
        return numberOfCities;
    }

    public String getProblemName() {
        return problemName;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }
}
