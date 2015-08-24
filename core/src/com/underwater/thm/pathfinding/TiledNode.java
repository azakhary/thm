package com.underwater.thm.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

/**
 * Created by azakhary on 8/22/2015.
 */
public abstract class TiledNode<N extends TiledNode<N>> implements IndexedNode<N> {

      /** The x coordinate of this tile */
    public final int col;

    /** The y coordinate of this tile */
    public final int row;

    protected Array<Connection<N>> connections;

    public TiledNode (int col, int row, Array<Connection<N>> connections) {
        this.col = col;
        this.row = row;
        this.connections = connections;
    }

    @Override
    public abstract int getIndex ();

    @Override
    public Array<Connection<N>> getConnections () {
        return this.connections;
    }

}