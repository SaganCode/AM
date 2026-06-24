import java.util.*;

public class TabuSearch {
    public static Solution run(TSPInstance inst, Solution initial, Random rnd,
            int maxIter, int tabuTenure, int neighborhoodSize) {
        int n = inst.n;
        Solution best = initial.clone(); best.evaluate(inst);
        Solution current = initial.clone(); current.evaluate(inst);

        // tabu list on edges swaps represented by pair key
        int[][] tabu = new int[n][n];
        int iter = 0;
        while (iter < maxIter) {
            Solution bestNeighbor = null;
            int bi=0,bj=0;
            double bestCost = Double.POSITIVE_INFINITY;
            // consider some random neighbors (limited)
            for (int k=0;k<neighborhoodSize;k++) {
                int i = rnd.nextInt(n);
                int j = rnd.nextInt(n);
                if (i==j) continue;
                int a=Math.min(i,j), b=Math.max(i,j);
                Solution cand = current.clone();
                cand.twoOpt(a,b);
                cand.evaluate(inst);
                boolean isTabu = tabu[a][b] > iter;
                if ((cand.cost < best.cost) || (!isTabu && cand.cost < bestCost)) {
                    bestNeighbor = cand;
                    bestCost = cand.cost;
                    bi=a; bj=b;
                }
            }
            if (bestNeighbor == null) break;
            current = bestNeighbor;
            if (current.cost < best.cost) best = current.clone();
            // mark move tabu
            tabu[bi][bj] = iter + tabuTenure;
            iter++;
            // System.out.println(current.evaluate(inst));
        }
        return best;
    }
}
