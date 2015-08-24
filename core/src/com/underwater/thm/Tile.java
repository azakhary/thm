package com.underwater.thm;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;

/**
 * Created by azakhary on 8/22/2015.
 */
public class Tile implements IndexedNode<Tile> {
    public int col;
    public int row;
    public Entity entity;

    public Tile(int col, int row, Entity entity) {
        this.col = col;
        this.row = row;
        this.entity = entity;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Array<Connection<Tile>> getConnections() {
        return null;
    }
}
