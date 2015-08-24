package com.underwater.thm.pathfinding.flat;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

/**
 * Created by CyberJoe on 8/22/2015.
 */
public class FlatTiledConnection extends DefaultConnection<FlatTiledNode> {
    public FlatTiledConnection(FlatTiledNode fromNode, FlatTiledNode toNode) {
        super(fromNode, toNode);
    }
}
