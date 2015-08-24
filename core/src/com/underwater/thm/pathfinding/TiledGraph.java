package com.underwater.thm.pathfinding;

import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.underwater.thm.Tile;

public interface TiledGraph<N extends TiledNode<N>> extends IndexedGraph<N> {

    public N getNode (int x, int y);

    public N getNode (int index);

}