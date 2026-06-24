import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Visualizer {
    public static void draw(TSPProblem problem, int[] tour, String filename) {
        int w = 1200, h = 800;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        double[][] coords = problem.getCoordinates();
        if (coords == null || coords.length == 0) {
            System.err.println("Brak wspolrzednych, nie mozna narysowac trasy.");
            return;
        }

        int n = problem.getNumberOfCities();
        double minx = Double.POSITIVE_INFINITY, miny = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            double x = coords[i][0];
            double y = coords[i][1];
            if (x < minx) minx = x;
            if (y < miny) miny = y;
            if (x > maxx) maxx = x;
            if (y > maxy) maxy = y;
        }

        double margin = 40;
        double sx = (w - 2 * margin) / (maxx - minx);
        double sy = (h - 2 * margin) / (maxy - miny);
        double s = Math.min(sx, sy);

        g.setStroke(new BasicStroke(2f));
        g.setColor(Color.BLUE);
        for (int i = 0; i < n; i++) {
            int a = tour[i];
            int b = tour[(i + 1) % n];
            int x1 = (int) ((coords[a][0] - minx) * s + margin);
            int y1 = (int) ((coords[a][1] - miny) * s + margin);
            int x2 = (int) ((coords[b][0] - minx) * s + margin);
            int y2 = (int) ((coords[b][1] - miny) * s + margin);
            g.drawLine(x1, h - y1, x2, h - y2);
        }

        g.dispose();
        try {
            ImageIO.write(img, "PNG", new File(filename));
        } catch (IOException e) {
            System.err.println("Failed to write image: " + e.getMessage());
        }
    }

    public static void show(TSPProblem problem, int[] tour) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TSP Visualizer");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new VisualizerPanel(problem, tour));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static class VisualizerPanel extends JPanel {
        private final TSPProblem problem;
        private final int[] tour;

        public VisualizerPanel(TSPProblem problem, int[] tour) {
            this.problem = problem;
            this.tour = tour.clone();
            setPreferredSize(new Dimension(1200, 800));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            double[][] coords = problem.getCoordinates();
            if (coords == null || coords.length == 0) {
                return;
            }

            int n = problem.getNumberOfCities();
            double minx = Double.POSITIVE_INFINITY, miny = Double.POSITIVE_INFINITY;
            double maxx = Double.NEGATIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                double x = coords[i][0];
                double y = coords[i][1];
                if (x < minx) minx = x;
                if (y < miny) miny = y;
                if (x > maxx) maxx = x;
                if (y > maxy) maxy = y;
            }

            double margin = 40;
            double sx = (getWidth() - 2 * margin) / (maxx - minx);
            double sy = (getHeight() - 2 * margin) / (maxy - miny);
            double s = Math.min(sx, sy);

            g.setStroke(new BasicStroke(2f));
            g.setColor(Color.BLUE);
            for (int i = 0; i < n; i++) {
                int a = tour[i];
                int b = tour[(i + 1) % n];
                int x1 = (int) ((coords[a][0] - minx) * s + margin);
                int y1 = (int) ((coords[a][1] - miny) * s + margin);
                int x2 = (int) ((coords[b][0] - minx) * s + margin);
                int y2 = (int) ((coords[b][1] - miny) * s + margin);
                g.drawLine(x1, getHeight() - y1, x2, getHeight() - y2);
            }
        }
    }
}
