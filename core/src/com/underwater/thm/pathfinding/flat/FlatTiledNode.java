package com.underwater.thm.pathfinding.flat;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import com.underwater.thm.pathfinding.TiledNode;

public class FlatTiledNode extends TiledNode<FlatTiledNode> {

    public int index;

    public FlatTiledNode (int col, int row, int connectionCapacity) {
        super(col, row, new Array<Connection<FlatTiledNode>>(connectionCapacity));

    }

    @Override
    public int getIndex () {
        return index;
    }

}