import java.util.*;

public class SimulatedAnnealing {
    public static Solution run(TSPInstance inst, Solution initial, Random rnd,
            double T0, double alpha, int iterPerTemp, int maxTempSteps) {
        Solution best = initial.clone();
        best.evaluate(inst);
        Solution current = initial.clone();
        current.evaluate(inst);
        double T = T0;
        int n = inst.n;
        for (int step=0; step<maxTempSteps && T>1e-4; step++) {
            for (int it=0; it<iterPerTemp; it++) {
                // generate neighbor by 2-opt
                int i = rnd.nextInt(n);
                int j = rnd.nextInt(n);
                if (i==j) continue;
                int a = Math.min(i,j), b = Math.max(i,j);
                Solution cand = current.clone();
                cand.twoOpt(a,b);
                cand.evaluate(inst);
                double delta = cand.cost - current.cost;
                if (delta < 0 || Math.exp(-delta / T) > rnd.nextDouble()) {
                    current = cand;
                    if (current.cost < best.cost) best = current.clone();
                }
            }
            T *= alpha;
            // System.out.println(current.evaluate(inst));
        }
        return best;
    }
}
