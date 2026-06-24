import java.util.*;

public class Solution implements Cloneable {
    public int[] tour;
    public double cost = -1;

    public Solution(int n) { tour = new int[n]; }

    public static Solution randomInitial(int n, Random rnd) {
        Solution s = new Solution(n);
        for (int i = 0; i < n; i++) s.tour[i]=i;
        for (int i = n-1; i>0; i--) {
            int j = rnd.nextInt(i+1);
            int t = s.tour[i]; s.tour[i]=s.tour[j]; s.tour[j]=t;
        }
        return s;
    }

    public double evaluate(TSPInstance inst) {
        double d = 0;
        int n = tour.length;
        for (int i = 0; i < n; i++) {
            int a = tour[i];
            int b = tour[(i+1)%n];
            d += inst.dist[a][b];
        }
        this.cost = d;
        return d;
    }

    public Solution clone() {
        Solution s = new Solution(tour.length);
        System.arraycopy(this.tour,0,s.tour,0,tour.length);
        s.cost = this.cost;
        return s;
    }

    // apply 2-opt between i (inclusive) and j (inclusive)
    public void twoOpt(int i, int j) {
        while (i<j) {
            int t = tour[i]; tour[i]=tour[j]; tour[j]=t;
            i++; j--;
        }
    }
}
