package com.underwater.thm;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.underwater.thm.Tile;
import com.underwater.thm.pathfinding.flat.FlatTiledNode;

/**
 * Created by azakhary on 8/22/2015.
 */
public class MinionComponent implements Component {

    public boolean spawned = false;
    public int pathIndex = 0;

    public Vector2 position;

    public Vector2 offset;

    public int hp = 20;
    public int maxHP = 20;

    public Array<FlatTiledNode> currPath;

    public float randomMsgCooldown;
}
