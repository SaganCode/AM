import java.io.*;
import java.util.*;

public class TSPInstance {
    public final int n;
    public final double[][] dist;
    public final double[][] coords;

    public TSPInstance(String path) throws IOException {
        List<double[]> coords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean inSection = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) continue;
                if (line.startsWith("NODE_COORD_SECTION")) { inSection = true; continue; }
                if (!inSection) continue;
                if (line.equals("EOF")) break;
                String[] parts = line.split("\\s+");
                if (parts.length < 3) continue;
                try {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    coords.add(new double[]{x,y});
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        this.n = coords.size();
        this.coords = new double[n][2];
        this.dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            this.coords[i][0] = coords.get(i)[0];
            this.coords[i][1] = coords.get(i)[1];
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i==j) dist[i][j]=0;
                else {
                    double dx = this.coords[i][0]-this.coords[j][0];
                    double dy = this.coords[i][1]-this.coords[j][1];
                    dist[i][j] = Math.hypot(dx,dy);
                }
            }
        }
    }
}
