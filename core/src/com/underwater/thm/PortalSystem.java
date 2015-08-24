package com.underwater.thm;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;
import com.uwsoft.editor.renderer.data.ParticleEffectVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;

/**
 * Created by azakhary on 8/22/2015.
 */
public class PortalSystem extends IteratingSystem {

    private ComponentMapper<PortalComponent> portalMapper = ComponentMapper.getFor(PortalComponent.class);
    private ComponentMapper<MinionComponent> minionMapper = ComponentMapper.getFor(MinionComponent.class);

    private NavigationMap navigationMap;
    private SceneLoader sl;

    public PortalSystem(SceneLoader sl, NavigationMap navigationMap) {
        super(Family.all(PortalComponent.class).get());
        this.navigationMap = navigationMap;
        this.sl = sl;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PortalComponent portalComponent = portalMapper.get(entity);
        ItemWrapper wrapper = new ItemWrapper(entity);
        TintComponent mainPartTint = ComponentRetriever.get(wrapper.getChild("main").getEntity(), TintComponent.class);

        // if it's sender then work with interval
        if(portalComponent.isIn) {

            if(portalComponent.currentCoolDown-deltaTime <= 0 && portalComponent.currentCoolDown > 0) {
                mainPartTint.color.a = 0.5f;
                TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
                DimensionsComponent sizeComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
                addParticle(transformComponent.x+sizeComponent.width/2, transformComponent.y+sizeComponent.height/2);
            }

            if(portalComponent.currentCoolDown <= 0) {
                // wooosh in progress
                float animStage = portalComponent.currentCoolDown * -1f;
                mainPartTint.color.a = animStage;
            }

            portalComponent.currentCoolDown-=deltaTime;
            if(portalComponent.currentCoolDown <= 0) {
                Array<Entity> caughtMinions = checkRange(entity);

                // sending to target (but before we do that need to change some stuff in pathfinding)
                Tile startPoint = navigationMap.tiledWorld.get(portalComponent.target);
                Tile endPoint = navigationMap.tiledWorld.getTilesByTag("end_point").first();

                for(Entity minion: caughtMinions) {
                    MinionComponent minionComponent = minionMapper.get(minion);
                    minionComponent.currPath = navigationMap.findPath(startPoint, endPoint);
                    minionComponent.pathIndex = 0;
                    TransformComponent minionTransform = ComponentRetriever.get(minion, TransformComponent.class);
                    TransformComponent portalTransform = ComponentRetriever.get(portalComponent.target, TransformComponent.class);
                    DimensionsComponent portalSize = ComponentRetriever.get(portalComponent.target, DimensionsComponent.class);
                    portalSize.height = 25;
                    portalSize.width = 25;
                    minionComponent.position.x = portalTransform.x + portalSize.width/2;
                    minionComponent.position.y = portalTransform.y + portalSize.height/2;
                }

                if(portalComponent.currentCoolDown < -1f) {
                    portalComponent.currentCoolDown = portalComponent.coolDown;
                    mainPartTint.color.a = 0.5f;
                }
            }
        }
    }

    private void addParticle(float x, float y) {
        ParticleEffectVO vo = new ParticleEffectVO();
        vo.particleName = "portalIn";
        vo.layerName = "decor";
        vo.x = x;
        vo.y = y;
        Entity effect = sl.entityFactory.createEntity(sl.getRoot(), vo);
        sl.getEngine().addEntity(effect);
        ParticleComponent particleComponent = ComponentRetriever.get(effect, ParticleComponent.class);
        particleComponent.particleEffect.start();

        DisposingParticleComponent disposingParticleComponent = new DisposingParticleComponent();
        disposingParticleComponent.duration = 2f;
        effect.add(disposingParticleComponent);
    }

    private Array<Entity> checkRange(Entity entity) {
        TransformComponent portalTransform = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent portalSize = ComponentRetriever.get(entity, DimensionsComponent.class);
        // do minion search
        Circle searchCircle = new Circle();
        searchCircle.x = portalTransform.x + portalSize.width/2;
        searchCircle.y = portalTransform.y + portalSize.height/2;
        searchCircle.radius = 5;

        Array<Entity> result = new Array<Entity>();

        Family minionsFamily = Family.all(MinionComponent.class).get();
        ImmutableArray<Entity> minions = getEngine().getEntitiesFor(minionsFamily);
        for(Entity minion: minions) {
            TransformComponent minionTransform = ComponentRetriever.get(minion, TransformComponent.class);
            MinionComponent minionComponent = minionMapper.get(minion);
            if(searchCircle.contains(minionTransform.x, minionTransform.y) && minionComponent.spawned) {
                result.add(minion);
                break;
            }
        }

        return result;
    }

    public static void preProcessPortals(SceneLoader sl) {
        Family minionsFamily = Family.all(PortalComponent.class).get();
        ImmutableArray<Entity> portals = sl.getEngine().getEntitiesFor(minionsFamily);

        HashMap<Integer, Entity> portalOuts = new HashMap<Integer, Entity>();

        for(Entity portal: portals) {
            PortalComponent portalComponent = portal.getComponent(PortalComponent.class);
            MainItemComponent mainItemComponent = ComponentRetriever.get(portal, MainItemComponent.class);
            portalComponent.load(mainItemComponent);
            if(!portalComponent.isIn) {
                portalOuts.put(portalComponent.portalId, portal);
            }
        }
        for(Entity portal: portals) {
            PortalComponent portalComponent = portal.getComponent(PortalComponent.class);
            if(portalComponent.isIn) {
                portalComponent.target = portalOuts.get(portalComponent.portalId);
            }
        }
    }
}
