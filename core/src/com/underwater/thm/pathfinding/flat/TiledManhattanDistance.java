package com.underwater.thm.pathfinding.flat;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.underwater.thm.pathfinding.TiledNode;

/** A Manhattan distance heuristic for a {@link com.underwater.thm.pathfinding.TiledGraph}. It simply calculates the Manhattan distance between two given
 * tiles.
 *
 * @param <N> Type of node, either flat or hierarchical, extending the {@link TiledNode} class
 *
 * @author davebaol */
public class TiledManhattanDistance<N extends TiledNode<N>> implements Heuristic<N> {

    public TiledManhattanDistance () {
    }

    @Override
    public float estimate (N node, N endNode) {
        return Math.abs(endNode.col - node.col) + Math.abs(endNode.row - node.row);
    }
}