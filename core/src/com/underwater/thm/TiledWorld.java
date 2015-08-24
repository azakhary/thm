package com.underwater.thm;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.HashMap;

/**
 * Created by azakhary on 8/22/2015.
 */
public class TiledWorld {

    public Tile[][] map;

    public int size;
    public int cols, rows;

    private HashMap<String, Array<Tile>> tagMap = new HashMap<String, Array<Tile>>();
    private HashMap<Entity, Tile> tileMap = new HashMap<Entity, Tile>();

    public TiledWorld(Engine engine, int cols, int rows, int size) {
        this.size = size;
        this.cols = cols;
        this.rows = rows;
        map = new Tile[cols][rows];
        initData(engine);
    }

    private void initData(Engine engine) {
        ImmutableArray<Entity> entities = engine.getEntities();

        for(int i = 0; i < entities.size(); i++) {
            MainItemComponent mainItemComponent = ComponentRetriever.get(entities.get(i), MainItemComponent.class);
            if(mainItemComponent.tags.contains("tile")) {
                TransformComponent transformComponent = ComponentRetriever.get(entities.get(i), TransformComponent.class);
                int col = (int) (transformComponent.x / size);
                int row = (int) (transformComponent.y / size);
                if(col < 0 || col > cols || row < 0 || row > rows) continue;
                map[col][row] = new Tile(col, row, entities.get(i));

                for(String tag: mainItemComponent.tags) {
                    if(!tagMap.containsKey(tag)) {
                        tagMap.put(tag, new Array<Tile>());
                    }

                    tagMap.get(tag).add(map[col][row]);
                    tileMap.put(entities.get(i), map[col][row]);
                }
            }
        }
    }

    public Tile get(Entity entity) {
        return tileMap.get(entity);
    }

    public Array<Tile> getTilesByTag(String tagName) {
        return tagMap.get(tagName);
    }

    public boolean isPath(int col, int row) {
        if(map[col][row] == null) return false;
        MainItemComponent mainItemComponent = ComponentRetriever.get(map[col][row].entity, MainItemComponent.class);
        if(mainItemComponent.tags.contains("path")) {
            return true;
        }

        return false;
    }

}
