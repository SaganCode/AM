import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class Visualizer {
    public static void draw(TSPInstance inst, Solution sol, String filename) {
        int w = 1200, h = 800;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,w,h);

        double minx=Double.POSITIVE_INFINITY, miny=Double.POSITIVE_INFINITY;
        double maxx=Double.NEGATIVE_INFINITY, maxy=Double.NEGATIVE_INFINITY;
        for (int i=0;i<inst.n;i++){
            double x = inst.coords[i][0], y = inst.coords[i][1];
            if (x<minx) minx=x; if (y<miny) miny=y;
            if (x>maxx) maxx=x; if (y>maxy) maxy=y;
        }
        double margin = 20;
        double sx = (w-2*margin) / (maxx-minx);
        double sy = (h-2*margin) / (maxy-miny);
        double s = Math.min(sx, sy);

        int radius = 4;
        // draw edges
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(Color.BLUE);
        for (int i=0;i<inst.n;i++){
            int a = sol.tour[i];
            int b = sol.tour[(i+1)%inst.n];
            int x1 = (int)((inst.coords[a][0]-minx)*s + margin);
            int y1 = (int)((inst.coords[a][1]-miny)*s + margin);
            int x2 = (int)((inst.coords[b][0]-minx)*s + margin);
            int y2 = (int)((inst.coords[b][1]-miny)*s + margin);
            g.drawLine(x1, h - y1, x2, h - y2);
        }

        // draw nodes
        g.setColor(Color.RED);
        for (int i=0;i<inst.n;i++){
            int x = (int)((inst.coords[i][0]-minx)*s + margin);
            int y = (int)((inst.coords[i][1]-miny)*s + margin);
            // g.fillOval(x-radius, h - y - radius, radius*2, radius*2);
        }

        g.dispose();
        try {
            ImageIO.write(img, "PNG", new File(filename));
        } catch (IOException e) {
            System.err.println("Failed to write image: "+e.getMessage());
        }
    }
}
