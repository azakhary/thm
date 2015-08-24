package com.underwater.thm;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.underwater.thm.pathfinding.flat.FlatTiledNode;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;
import com.uwsoft.editor.renderer.data.ParticleEffectVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by CyberJoe on 8/22/2015.
 */
public class MinionSystem extends IteratingSystem {

    private NavigationMap navigationMap;
    private ComponentMapper<MinionComponent> minionMapper = ComponentMapper.getFor(MinionComponent.class);

    private SceneLoader sl;

    private final float speed = 50f;

    private Array<String> languageLibrary = new Array<String>();

    public MinionSystem(SceneLoader sl, NavigationMap navigationMap) {
        super(Family.all(MinionComponent.class).get());
        this.navigationMap = navigationMap;
        this.sl = sl;

        languageLibrary.add("Brothers !!!");
        languageLibrary.add("PRAY!!!");
        languageLibrary.add("No to physics!");
        languageLibrary.add("Burn the Witches!");
        languageLibrary.add("Evolution is folly!");
        languageLibrary.add("Hallelujah!");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MinionComponent minion = minionMapper.get(entity);
        TransformComponent minionTransform = ComponentRetriever.get(entity, TransformComponent.class);
        ZIndexComponent minionIndex = ComponentRetriever.get(entity, ZIndexComponent.class);
        ItemWrapper minionWrapper = new ItemWrapper(entity);
        Entity imageEntity = minionWrapper.getChild("image").getEntity();
        TransformComponent imageTransform = ComponentRetriever.get(imageEntity, TransformComponent.class);
        DimensionsComponent imageSize = ComponentRetriever.get(imageEntity, DimensionsComponent.class);

        if(!minion.spawned) {
            FlatTiledNode node = minion.currPath.get(minion.pathIndex);
            Tile currTile = navigationMap.tiledWorld.map[node.col][node.row];

            // let's spawn the minion to curr tile
            TransformComponent tileTransform = ComponentRetriever.get(currTile.entity, TransformComponent.class);
            DimensionsComponent tileSize =  ComponentRetriever.get(currTile.entity, DimensionsComponent.class);

            minion.position = new Vector2();
            minion.offset = new Vector2();
            minion.position.x = tileTransform.x + tileSize.width/2;
            minion.position.y = tileTransform.y + tileSize.height/2;

            minion.randomMsgCooldown = MathUtils.random(1f, 7f);

            imageTransform.originX = imageSize.width/2f;
            imageTransform.originY = 0;

            minion.spawned = true;
        }

        if(minion.hp <= 0) {
            destroyMinion(entity);
            return;
        }

        TintComponent healthTint = ComponentRetriever.get(minionWrapper.getChild("health").getEntity(), TintComponent.class);
        TransformComponent healthTransform = ComponentRetriever.get(minionWrapper.getChild("health").getEntity(), TransformComponent.class);
        healthTint.color = new Color(1, 0, 0, 1);
        healthTransform.scaleX = (float)minion.hp/minion.maxHP * 80f;

        // bubble text
        minion.randomMsgCooldown-=deltaTime;

        if(minion.randomMsgCooldown < 0 && minion.randomMsgCooldown >= -3) {
            Entity bubbleEntity = minionWrapper.getChild("bubble").getEntity();
            Entity lblEntity = minionWrapper.getChild("bubble").getChild("lbl").getEntity();
            MainItemComponent main = ComponentRetriever.get(bubbleEntity, MainItemComponent.class);
            if(!main.visible) {
                if(MathUtils.random(0, 10) > 7) {
                    main.visible = true;
                    LabelComponent labelComponent = ComponentRetriever.get(lblEntity, LabelComponent.class);
                    TransformComponent transformLbl = ComponentRetriever.get(bubbleEntity, TransformComponent.class);
                    labelComponent.setText(languageLibrary.get(MathUtils.random(0, languageLibrary.size - 1)));
                    transformLbl.y = MathUtils.random(18, 29);
                } else {
                    minion.randomMsgCooldown = MathUtils.random(0.5f, 4f);
                }
            }
        } else if(minion.randomMsgCooldown < -3){
            minion.randomMsgCooldown = MathUtils.random(0.5f, 4f);
        } else {
            Entity bubbleEntity = minionWrapper.getChild("bubble").getEntity();
            MainItemComponent main = ComponentRetriever.get(bubbleEntity, MainItemComponent.class);
            main.visible = false;
        }

        // now that minion is spawned it's time to move it to next tile.
        if(minion.pathIndex < minion.currPath.size-1) { // end is not yet reached
            FlatTiledNode currNode = minion.currPath.get(minion.pathIndex);
            FlatTiledNode nextNode = minion.currPath.get(minion.pathIndex+1);
            Tile currTile = navigationMap.tiledWorld.map[currNode.col][currNode.row];
            Tile nextTile = navigationMap.tiledWorld.map[nextNode.col][nextNode.row];


            // finding tile size and location
            TransformComponent tileTransform = ComponentRetriever.get(nextTile.entity, TransformComponent.class);
            DimensionsComponent tileSize =  ComponentRetriever.get(nextTile.entity, DimensionsComponent.class);
            tileSize.height = 25;
            tileSize.width = 25;

            // moving towards that tile with constant speed by delta time
            // first decide on direction.
            Vector2 pointFrom = new Vector2(minion.position.x, minion.position.y);
            Vector2 pointTo = new Vector2(  tileTransform.x + tileSize.width/2,
                                            tileTransform.y + tileSize.height/2);

            int verticalDirection = 0;
            int horizontalDirection = 0;
            if(pointFrom.x == pointTo.x && pointFrom.y > pointTo.y) {
                verticalDirection = -1;
            }
            if(pointFrom.x == pointTo.x && pointFrom.y < pointTo.y) {
                verticalDirection = 1;
            }
            if(pointFrom.y == pointTo.y && pointFrom.x > pointTo.x) {
                horizontalDirection = -1;
            }
            if(pointFrom.y == pointTo.y && pointFrom.x < pointTo.x) {
                horizontalDirection = 1;
            }

            if((horizontalDirection == 1 && imageTransform.scaleX < 0) || (horizontalDirection == -1 && imageTransform.scaleX > 0)) {
                imageTransform.scaleX *= -1f;
            }

            if((horizontalDirection == 1 && minion.position.x + speed * deltaTime > pointTo.x) ||
               (horizontalDirection == -1 && minion.position.x - speed * deltaTime < pointTo.x) ||
               (verticalDirection == 1 && minion.position.y + speed * deltaTime > pointTo.y) ||
               (verticalDirection == -1 && minion.position.y - speed * deltaTime < pointTo.y)) {

                minion.position.x = tileTransform.x + tileSize.width/2;
                minion.position.y = tileTransform.y + tileSize.height/2;

                minion.pathIndex++;
            } else {
                minion.position.x += horizontalDirection * speed * deltaTime;
                minion.position.y += verticalDirection * speed * deltaTime;
            }
        } else {
            getEngine().removeEntity(entity);
            gameWin();
            return;
        }

        minion.offset.x += MathUtils.random(-1, 1) * 10f * deltaTime;
        minion.offset.y += MathUtils.random(-1, 1) * 10f * deltaTime;

        if(minion.offset.x > 15f) minion.offset.x-=10f * deltaTime;
        if(minion.offset.x < -15f) minion.offset.x+=10f * deltaTime;
        if(minion.offset.y > 15f) minion.offset.y-=10f * deltaTime;
        if(minion.offset.y < -15f) minion.offset.y+=10f * deltaTime;

        minionTransform.x = minion.position.x + minion.offset.x;
        minionTransform.y = minion.position.y + minion.offset.y;

    }

    private void gameWin() {
        ItemWrapper root = new ItemWrapper(sl.getRoot());
        Entity citadel = root.getChild("citadel").getEntity();
        CitadelComponent citadelComponent = citadel.getComponent(CitadelComponent.class);
        citadelComponent.riseFlag();


    }

    private void destroyMinion(Entity entity) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);

        createSplatter(transformComponent.x, transformComponent.y);

        SoundMgr.getInstance().play("MonkDie");

        getEngine().removeEntity(entity);
    }

    private void createSplatter(float x, float y) {
        ParticleEffectVO vo = new ParticleEffectVO();
        vo.particleName = "blood";
        vo.layerName = "decor";
        vo.x = x;
        vo.y = y;
        Entity splatter = sl.entityFactory.createEntity(sl.getRoot(), vo);
        sl.getEngine().addEntity(splatter);
        ParticleComponent particleComponent = ComponentRetriever.get(splatter, ParticleComponent.class);
        particleComponent.particleEffect.start();

        DisposingParticleComponent disposingParticleComponent = new DisposingParticleComponent();
        disposingParticleComponent.duration = 4f;
        splatter.add(disposingParticleComponent);
    }
}
