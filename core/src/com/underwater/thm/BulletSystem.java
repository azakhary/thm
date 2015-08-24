package com.underwater.thm;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by CyberJoe on 8/23/2015.
 */
public class BulletSystem extends IteratingSystem {

    private ComponentMapper<BulletComponent> mapper = ComponentMapper.getFor(BulletComponent.class);

    public BulletSystem() {
        super(Family.all(BulletComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        BulletComponent bulletComponent = mapper.get(entity);

        float distance = deltaTime * bulletComponent.speed;
        float xDistance = distance * MathUtils.cosDeg(bulletComponent.getAngle());
        float yDistance = distance * MathUtils.sinDeg(bulletComponent.getAngle());

        transformComponent.x += xDistance;
        transformComponent.y += yDistance;

        // check for collision
        Vector2 diff = new Vector2(bulletComponent.to.x - transformComponent.x, bulletComponent.to.y - transformComponent.y);
        if(diff.len() < 1f) {
            getEngine().removeEntity(entity);
        }
    }
}
