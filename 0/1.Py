import tsplib95
import numpy as np
import matplotlib.pyplot as plt
from itertools import combinations


def draw_nodes_and_edges(node_coords, edges, title='Graph', figsize=(10, 8), node_color='blue', edge_color='gray', node_size=40, annotate=True, show=True):
    fig, ax = plt.subplots(figsize=figsize)
    xs = []
    ys = []
    labels = []
    for node, coord in node_coords.items():
        xs.append(coord[0])
        ys.append(coord[1])
        labels.append(node)

    ax.scatter(xs, ys, s=node_size, c=node_color, zorder=2)
    if annotate:
        for label, x, y in zip(labels, xs, ys):
            ax.annotate(str(label), (x, y), textcoords='offset points',
                        xytext=(2, 2), fontsize=8)

    for u, v in edges:
        if u not in node_coords or v not in node_coords:
            continue
        x1, y1 = node_coords[u]
        x2, y2 = node_coords[v]
        ax.plot([x1, x2], [y1, y2], color=edge_color, linewidth=1, zorder=1)

    ax.set_aspect('equal', adjustable='box')
    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    if title:
        ax.set_title(title)
    ax.grid(True, linestyle='--', alpha=0.3)

    if show:
        plt.show()
    return fig, ax


def kruskal_mst(nodes, weight_func):
    parent = {node: node for node in nodes}
    rank = {node: 0 for node in nodes}

    def find(node):
        if parent[node] != node:
            parent[node] = find(parent[node])
        return parent[node]

    def union(u, v):
        root_u = find(u)
        root_v = find(v)
        if root_u == root_v:
            return False
        if rank[root_u] < rank[root_v]:
            parent[root_u] = root_v
        elif rank[root_u] > rank[root_v]:
            parent[root_v] = root_u
        else:
            parent[root_v] = root_u
            rank[root_u] += 1
        return True

    weighted_edges = []
    for u, v in combinations(nodes, 2):
        weighted_edges.append((weight_func(u, v), u, v))
    weighted_edges.sort(key=lambda item: item[0])

    mst_edges = []
    total_weight = 0
    for weight, u, v in weighted_edges:
        if union(u, v):
            mst_edges.append((u, v))
            total_weight += weight
            if len(mst_edges) == len(nodes) - 1:
                break

    return mst_edges, total_weight


def mst_to_tsp_cycle(mst_edges, start_node=None):
    # Build adjacency list from MST edges
    adj = {}
    for u, v in mst_edges:
        adj.setdefault(u, []).append(v)
        adj.setdefault(v, []).append(u)
    
    # DFS to get a path visiting all nodes
    visited = set()
    path = []
    
    def dfs(node):
        visited.add(node)
        path.append(node)
        for neighbor in adj.get(node, []):
            if neighbor not in visited:
                dfs(neighbor)
    
    if start_node is None:
        start_node = list(adj.keys())[0] if adj else None
    if start_node:
        dfs(start_node)
    
    # Close the cycle by adding the start node at the end
    if path:
        path.append(path[0])
    
    return path


def find_best_mst_tsp_cycle(mst_edges, weight_func):
    adj = {}
    for u, v in mst_edges:
        adj.setdefault(u, []).append(v)
        adj.setdefault(v, []).append(u)
    
    nodes = list(adj.keys())
    best_cycle = None
    best_weight = float('inf')
    
    for start in nodes:
        cycle = mst_to_tsp_cycle(mst_edges, start_node=start)
        if cycle:
            cycle_edges = [(cycle[i], cycle[i+1]) for i in range(len(cycle)-1)]
            weight = sum(weight_func(u, v) for u, v in cycle_edges)
            if weight < best_weight:
                best_weight = weight
                best_cycle = cycle
    
    return best_cycle, best_weight


problem = tsplib95.load('wi29.tsp')

list_of_nodes = list(problem.get_nodes())
list_of_edges = problem.get_edges()
list_of_weights = list()
permutations = []


for i in range(0, 1000):
    weight = 0
    seed_permutation = list_of_nodes.copy()
    np.random.seed(i)
    np.random.shuffle(seed_permutation)
    permutations.append(seed_permutation.copy())
    for j in range(len(seed_permutation)-1):
        weight += problem.get_weight(seed_permutation[j], seed_permutation[j+1])
    weight += problem.get_weight(seed_permutation[-1], seed_permutation[0])
    list_of_weights.append(weight)


group_of_10_list = list()
for i in range(0, 100):
    min_of_10 = 10000000000000
    for j in range(1, 11):
        if list_of_weights[i*10 + j-1] < min_of_10:
            min_of_10 = list_of_weights[i*10 + j-1]
    group_of_10_list.append(min_of_10)

group_of_50_list = list()
for i in range(0, 20):
    min_of_50 = 10000000000000
    for j in range(1, 51):
        if list_of_weights[i*50 + j-1] < min_of_50:
            min_of_50 = list_of_weights[i*50 + j-1]
    group_of_50_list.append(min_of_50)

print("\n100 grups of 10:")
for i in range(0, len(group_of_10_list)):
    print(i+1, ":", group_of_10_list[i])

print("\n20 grups of 50:")
for i in range(0, len(group_of_50_list)):
    print(i+1, ":", group_of_50_list[i])

print("\nminimum of 1000 random permutations:")
print(min(list_of_weights))




index = list_of_weights.index(min(list_of_weights))
first_perm = permutations[index]
path_edges = [(first_perm[i], first_perm[i+1]) for i in range(len(first_perm)-1)]
path_edges.append((first_perm[-1], first_perm[0]))
draw_nodes_and_edges(dict(problem.node_coords), path_edges, title='Random permutation tour', edge_color='red', node_color='blue', node_size=30)

mst_edges, mst_weight = kruskal_mst(list_of_nodes, problem.get_weight)
print(f'Kruskal MST total weight: {mst_weight}')
draw_nodes_and_edges(dict(problem.node_coords), mst_edges, title=f'Kruskal MST (weight={mst_weight})', edge_color='green', node_color='blue', node_size=30)

# Convert MST to TSP cycle
tsp_cycle = mst_to_tsp_cycle(mst_edges)
if tsp_cycle:
    cycle_edges = [(tsp_cycle[i], tsp_cycle[i+1]) for i in range(len(tsp_cycle)-1)]
    cycle_weight = sum(problem.get_weight(u, v) for u, v in cycle_edges)
    print(f'TSP cycle from MST total weight: {cycle_weight}')
    draw_nodes_and_edges(dict(problem.node_coords), cycle_edges, title=f'TSP cycle from MST (weight={cycle_weight})', edge_color='purple', node_color='blue', node_size=30)

# Find the best TSP cycle by trying all starting nodes
best_cycle, best_weight = find_best_mst_tsp_cycle(mst_edges, problem.get_weight)
if best_cycle:
    best_edges = [(best_cycle[i], best_cycle[i+1]) for i in range(len(best_cycle)-1)]
    print(f'Best TSP cycle from MST total weight: {best_weight}')
    draw_nodes_and_edges(dict(problem.node_coords), best_edges, title=f'Best TSP cycle from MST (weight={best_weight})', edge_color='orange', node_color='blue', node_size=30)
