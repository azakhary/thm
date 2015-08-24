package com.underwater.thm;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by CyberJoe on 8/23/2015.
 */
public class DisposingParticleSystem extends IteratingSystem {

    private ComponentMapper<DisposingParticleComponent> mapper = ComponentMapper.getFor(DisposingParticleComponent.class);

    public DisposingParticleSystem() {
        super(Family.all(DisposingParticleComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DisposingParticleComponent component = mapper.get(entity);
        component.duration -= deltaTime;

        if(component.duration <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}
