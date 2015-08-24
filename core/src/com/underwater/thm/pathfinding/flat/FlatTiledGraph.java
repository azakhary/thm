package com.underwater.thm.pathfinding.flat;

import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.underwater.thm.Tile;
import com.underwater.thm.TiledWorld;
import com.underwater.thm.pathfinding.TiledGraph;

public class FlatTiledGraph extends DefaultIndexedGraph<FlatTiledNode> implements TiledGraph<FlatTiledNode> {
    public int rows;
    public int cols;

    private TiledWorld tiledWorld;

    private FlatTiledNode[][] map;

    public FlatTiledGraph (TiledWorld tiledWorld) {
        super(tiledWorld.rows * tiledWorld.cols);
        this.tiledWorld = tiledWorld;
        this.rows = tiledWorld.rows;
        this.cols = tiledWorld.cols;

        map = new FlatTiledNode[cols][rows];
    }

    public void init () {
        for(int c = 0; c < cols; c++) {
            for(int r = 0; r < rows; r++) {
                if(tiledWorld.isPath(c, r)) {
                    FlatTiledNode node = new FlatTiledNode(c, r, 4);
                    node.index = nodes.size;
                    nodes.add(node);
                    map[c][r] = node;
                }
            }
        }

        for(FlatTiledNode n : nodes) {
            int row = n.row;
            int col = n.col;
            if(n.col > 0 && map[n.col-1][n.row] != null) addConnection(n, -1, 0);
            if(n.row > 0 && map[n.col][n.row-1] != null) addConnection(n, 0, -1);
            if(n.col < cols && map[n.col+1][n.row] != null) addConnection(n, 1, 0);
            if(n.row < rows && map[n.col][n.row+1] != null) addConnection(n, 0, 1);
        }
    }

    @Override
    public FlatTiledNode getNode (int col, int row) {
        return map[col][row];
    }

    public FlatTiledNode getNode (Tile tile) {
        return getNode(tile.col, tile.row);
    }

    @Override
    public FlatTiledNode getNode (int index) {
        return nodes.get(index);
    }

    private void addConnection (FlatTiledNode n, int colOffset, int rowOffset) {
        FlatTiledNode target = getNode(n.col + colOffset, n.row + rowOffset);
        n.getConnections().add(new FlatTiledConnection(n, target));
    }

}