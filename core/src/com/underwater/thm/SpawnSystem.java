package com.underwater.thm;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.underwater.thm.MinionComponent;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.SimpleImageVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by azakhary on 8/22/2015.
 */
public class SpawnSystem extends EntitySystem {

    private Viewport viewport;
    private SceneLoader sl;
    private NavigationMap navigationMap;
    private Thm game;

    private float coolDown = 1f;

    private int minionCount = 30;
    private int currMinions;

    private Array<String> minionList = new Array<String>();

    public SpawnSystem(Thm game, Viewport viewport, SceneLoader sl,NavigationMap navigationMap) {
        this.sl = sl;
        this.viewport = viewport;
        this.navigationMap = navigationMap;
        this.game = game;

        currMinions = minionCount;

        minionList.add("minion_fat");
        minionList.add("minion_inq");
        minionList.add("minion_sis");
        minionList.add("minion_cross");
    }

    @Override
    public void update (float deltaTime) {
        if(sl.getRoot() == null) return;

        coolDown-=deltaTime;
        if(coolDown < 0) {
            coolDown = 0;
        }

        if(coolDown == 0 && Gdx.input.justTouched()) {
            Tile start = touchedStartPoint();
            if(start != null) {
                if (currMinions <= 0) {
                    checkIfGameEnded();
                } else {
                    spawnMinion(start);
                    coolDown = 0.2f;
                }
            }
        }

        ItemWrapper root = new ItemWrapper(sl.getRoot());
        LabelComponent labelComponent = ComponentRetriever.get(root.getChild("minionsLbl").getEntity(), LabelComponent.class);
        labelComponent.setText("fanatics available: " + currMinions);

        Family minionsFamily = Family.all(MinionComponent.class).get();
        ImmutableArray<Entity> minions = getEngine().getEntitiesFor(minionsFamily);
        if(minions.size() == 0) {
            game.checkIfWin();
        }
    }

    private void checkIfGameEnded() {
        Family minionsFamily = Family.all(MinionComponent.class).get();
        ImmutableArray<Entity> minions = getEngine().getEntitiesFor(minionsFamily);

        if(minions.size() == 0) {
            currMinions = minionCount;
            coolDown = 0.2f;
        }
    }

    private Tile touchedStartPoint() {
        Array<Tile> startPoints = navigationMap.tiledWorld.getTilesByTag("start_point");
        if(startPoints == null) return null;
        for (Tile tile: startPoints) {
            TransformComponent transformComponent = ComponentRetriever.get(tile.entity, TransformComponent.class);
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(tile.entity, DimensionsComponent.class);
            Rectangle rect = new Rectangle(transformComponent.x, transformComponent.y, dimensionsComponent.width, dimensionsComponent.height);
            Vector2 tapPoint = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(tapPoint);
            if(rect.contains(tapPoint)) {
                return tile;
            }
        }

        return null;
    }

    private void spawnMinion(Tile startPoint) {
        Tile endPoint = navigationMap.tiledWorld.getTilesByTag("end_point").first();

        CompositeItemVO minionData =  sl.loadVoFromLibrary(minionList.get(MathUtils.random(0, minionList.size-1)));
        minionData.layerName = "characters";
        minionData.y = -300;
        Entity minion = sl.entityFactory.createEntity(sl.getRoot(), minionData);
        sl.entityFactory.initAllChildren(getEngine(), minion, minionData.composite);
        sl.getEngine().addEntity(minion);
        MinionComponent minionComponent = new MinionComponent();

        minionComponent.currPath = navigationMap.findPath(startPoint, endPoint);

        minion.add(minionComponent);

        currMinions--;

        SoundMgr.getInstance().play("MonkSpawn");
    }

    public void setMinions(int minions) {
        currMinions = minions;
    }
}
