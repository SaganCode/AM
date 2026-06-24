import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "qa194.tsp";
        if (args.length>0) path = args[0];
        System.out.println("Reading: " + path);
        TSPInstance inst = new TSPInstance(path);
        System.out.println("Nodes: " + inst.n);
        int trials = 1;
        Random rnd = new Random(12345);

        // Simulated Annealing parameters
        double T0 = 10000;
        double alpha = 0.995;
        int iterPerTemp = Math.max(100, inst.n*10);
        int maxTempSteps = 2000;

        // Tabu parameters
        int tabuIter = Math.max(500, inst.n*10);
        int tabuTenure = Math.max(7, inst.n/10);
        int neighborhoodSize = Math.max(50, inst.n*2);

        System.out.println("Running Simulated Annealing ("+trials+" trials)...");
        double sumSA = 0; double bestSA = Double.POSITIVE_INFINITY; Solution bestSASol=null;
        double globalBestSA = Double.POSITIVE_INFINITY; Solution globalBestSol = null;
        double globalBestT = Double.POSITIVE_INFINITY;
        for (int t=0;t<trials;t++) {
            Solution init = Solution.randomInitial(inst.n, new Random(rnd.nextLong()));
            init.evaluate(inst);
            double initCost = init.cost;
            Solution res = SimulatedAnnealing.run(inst, init, new Random(rnd.nextLong()), T0, alpha, iterPerTemp, maxTempSteps);
            sumSA += res.cost;
            boolean improved = false;
            double improveFromInit = initCost - res.cost;
            double improveFromBest = Double.NaN;
            if (res.cost < bestSA) {
                improveFromBest = bestSA - res.cost;
                bestSA = res.cost; bestSASol = res;
                improved = true;
            } else {
                improveFromBest = res.cost - bestSA; // positive means worse
            }
            // update global best (single image)
            if (res.cost < globalBestSA) {
                globalBestSA = res.cost;
                globalBestSol = res.clone();
                String fn = "SA_best_overall.png";
                Visualizer.draw(inst, res, fn);
                System.out.printf("Saved GLOBAL best image: %s (SA trial %d)\n", fn, t+1);
            }
            System.out.printf("SA trial %3d/%d: init=%.3f -> result=%.3f, improvement vs init=%.3f, %s bestSoFar=%.3f\n",
                    t+1, trials, initCost, res.cost, improveFromInit, (improved?"NEW BEST":""), bestSA);
        }
        System.out.printf("SA best=%.3f avg=%.3f\n", bestSA, sumSA/trials);

        System.out.println("Running Tabu Search ("+trials+" trials)...");
        double sumTabu = 0; double bestTabu = Double.POSITIVE_INFINITY; Solution bestTabuSol=null;
        for (int t=0;t<trials;t++) {
            Solution init = Solution.randomInitial(inst.n, new Random(rnd.nextLong()));
            init.evaluate(inst);
            double initCost = init.cost;
            Solution res = TabuSearch.run(inst, init, new Random(rnd.nextLong()), tabuIter, tabuTenure, neighborhoodSize);
            sumTabu += res.cost;
            boolean improved = false;
            double improveFromInit = initCost - res.cost;
            if (res.cost < bestTabu) {
                improved = true;
                bestTabu = res.cost; bestTabuSol = res;
            }
            // update global best (single image)
            if (res.cost < globalBestT) {
                globalBestT = res.cost;
                globalBestSol = res.clone();
                String fn = "Tabu_best_overall.png";
                Visualizer.draw(inst, res, fn);
                System.out.printf("Saved GLOBAL best image: %s (Tabu trial %d)\n", fn, t+1);
            }
            System.out.printf("Tabu trial %3d/%d: init=%.3f -> result=%.3f, improvement vs init=%.3f, %s bestSoFar=%.3f\n",
                    t+1, trials, initCost, res.cost, improveFromInit, (improved?"NEW BEST":""), bestTabu);
        }
        System.out.printf("Tabu best=%.3f avg=%.3f\n", bestTabu, sumTabu/trials);

        System.out.println("Done.");
    }
}
