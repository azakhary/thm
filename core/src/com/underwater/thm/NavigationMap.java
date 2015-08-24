package com.underwater.thm;


import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.utils.Array;
import com.underwater.thm.pathfinding.flat.*;

/**
 * Created by azakhary on 8/22/2015.
 */
public class NavigationMap {

    private FlatTiledGraph graph;

    public TiledWorld tiledWorld;

    public NavigationMap() {

    }

    public void init(TiledWorld tiledWorld) {
        this.tiledWorld = tiledWorld; // rethink this shit when things work.
        graph = new FlatTiledGraph(tiledWorld);
        graph.init();
    }

    public Array<FlatTiledNode> findPath(Tile startPoint, Tile endPoint) {
        TiledSmoothableGraphPath<FlatTiledNode> path =  new TiledSmoothableGraphPath<FlatTiledNode>();
        TiledManhattanDistance<FlatTiledNode> heuristic = new TiledManhattanDistance<FlatTiledNode>();
        IndexedAStarPathFinder<FlatTiledNode> pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(graph, true);
        path.clear();
        pathFinder.searchNodePath(graph.getNode(startPoint), graph.getNode(endPoint), heuristic, path);

        return path.nodes;
    }
}
