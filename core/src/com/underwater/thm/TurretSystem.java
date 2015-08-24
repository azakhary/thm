package com.underwater.thm;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;
import com.uwsoft.editor.renderer.components.sprite.AnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.ParticleEffectVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by azakhary on 8/22/2015.
 */
public class TurretSystem extends IteratingSystem {

    private ComponentMapper<TurretComponent> turretMapper = ComponentMapper.getFor(TurretComponent.class);
    private ComponentMapper<MinionComponent> minionMapper = ComponentMapper.getFor(MinionComponent.class);

    private SceneLoader sl;

    public TurretSystem(SceneLoader sl) {
        super(Family.all(TurretComponent.class).get());
        this.sl = sl;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TurretComponent turret = turretMapper.get(entity);
        TransformComponent turretTransform = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent turretSize = ComponentRetriever.get(entity, DimensionsComponent.class);
        ItemWrapper turretWrapper = new ItemWrapper(entity);
        Entity mainEntity = turretWrapper.getChild("main").getEntity();
        TransformComponent mainPartTransform = ComponentRetriever.get(mainEntity, TransformComponent.class);

        SpriteAnimationStateComponent animationComponent = ComponentRetriever.get(mainEntity, SpriteAnimationStateComponent.class);

        if(turret.state == TurretComponent.TurretState.OFF) {
            animationComponent.paused = true;
            return;
        }

        turret.coolDown-=deltaTime;
        if(turret.coolDown < 0) turret.coolDown = 0;

        Circle searchCircle = new Circle();
        searchCircle.x = turretTransform.x + turretSize.width/2;
        searchCircle.y = turretTransform.y + turretSize.height/2;
        searchCircle.radius = turret.radius;

        if(turret.state == TurretComponent.TurretState.NONE) {
            mainPartTransform.rotation = MathUtils.random(0, 360);
            mainPartTransform.originX = 8;
            mainPartTransform.originY = 12;
            turret.state = TurretComponent.TurretState.SEARCHING;
        }

        if(turret.state == TurretComponent.TurretState.SEARCHING) {
            Family minionsFamily = Family.all(MinionComponent.class).get();
            ImmutableArray<Entity> minions = getEngine().getEntitiesFor(minionsFamily);
            for(Entity minion: minions) {
                TransformComponent minionTransform = ComponentRetriever.get(minion, TransformComponent.class);
                MinionComponent minionComponent = minionMapper.get(minion);
                if(searchCircle.contains(minionTransform.x, minionTransform.y) && minionComponent.spawned) {
                    turret.state = TurretComponent.TurretState.LOCKED;
                    turret.target = minion;
                    break;
                }
            }
            animationComponent.paused = true;

            turret.timeIdle+=deltaTime;
            if(turret.timeIdle > 0.6f) {
                //mainPartTransform.rotation += deltaTime * 10f;
            }
        }

        // check if target is out of range
        if(turret.state == TurretComponent.TurretState.LOCKED) {
            if(turret.target == null) {
                turret.state = TurretComponent.TurretState.SEARCHING;
            } else {
                TransformComponent minionTransform = ComponentRetriever.get(turret.target, TransformComponent.class);
                if(!searchCircle.contains(minionTransform.x, minionTransform.y)) {
                    turret.state = TurretComponent.TurretState.SEARCHING;
                    turret.target = null;
                }
            }
        }

        // if locked rotate towards the enemy
        if(turret.state == TurretComponent.TurretState.LOCKED) {
            turret.timeIdle = 0;
            TransformComponent minionTransform = ComponentRetriever.get(turret.target, TransformComponent.class);

            Vector2 minionPos = new Vector2(minionTransform.x, minionTransform.y);
            Vector2 turretPos = new Vector2(turretTransform.x+mainPartTransform.x+8, turretTransform.y+mainPartTransform.y+12);

            Vector2 diff = new Vector2(minionPos.x - turretPos.x, minionPos.y - turretPos.y);
            float angle = diff.angle();

            if(angle > 360) angle = angle-360;
            if(angle > 180) angle = -(360-angle);

            if(mainPartTransform.rotation > angle) {
                mainPartTransform.rotation -= turret.rotationSpeed * deltaTime;
                if(mainPartTransform.rotation < angle) mainPartTransform.rotation = angle;
            }
            if(mainPartTransform.rotation < angle) {
                mainPartTransform.rotation += turret.rotationSpeed * deltaTime;
                if(mainPartTransform.rotation > angle) mainPartTransform.rotation = angle;
            }


            // now that we are rotated let's see if we are pointing to the enemy
            if(Math.abs(mainPartTransform.rotation - angle) < 2) {
                if(turret.coolDown == 0) {
                    // shoot
                    SoundMgr.getInstance().play("Shot" + MathUtils.random(1, 5), 0.3f);
                    createPulse(turretPos, minionPos);
                    animationComponent.paused = false;
                    MinionComponent minionComponent = minionMapper.get(turret.target);
                    minionComponent.hp-=turret.power;
                    turret.coolDown = 0.2f;
                    if (minionComponent.hp <= 0) {
                        turret.target = null;
                        turret.state = TurretComponent.TurretState.SEARCHING;
                    }
                }
            }
        }
    }

    private void createPulse(Vector2 from, Vector2 to) {
        ParticleEffectVO vo = new ParticleEffectVO();
        vo.particleName = "pulse";
        vo.layerName = "decor";
        vo.x = from.x;
        vo.y = from.y;
        Entity pulse = sl.entityFactory.createEntity(sl.getRoot(), vo);
        sl.getEngine().addEntity(pulse);
        ParticleComponent particleComponent = ComponentRetriever.get(pulse, ParticleComponent.class);
        particleComponent.particleEffect.start();

        DisposingParticleComponent disposingParticleComponent = new DisposingParticleComponent();
        disposingParticleComponent.duration = 4f;
        pulse.add(disposingParticleComponent);

        BulletComponent bulletComponent = new BulletComponent(from, to);
        pulse.add(bulletComponent);

    }
}
