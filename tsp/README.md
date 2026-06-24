Java TSP heuristics

Build:

javac -d out src\*.java

Run (reads qa194.tsp from current folder by default):

java -cp out Main qa194.tsp

The program runs 100 trials of Simulated Annealing and Tabu Search and prints best and average tour lengths.
When a new global best solution is found, it saves one image file named `best_overall.png`.
