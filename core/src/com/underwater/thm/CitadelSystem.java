package com.underwater.thm;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by CyberJoe on 8/23/2015.
 */
public class CitadelSystem extends IteratingSystem {

    private ComponentMapper<CitadelComponent> mapper = ComponentMapper.getFor(CitadelComponent.class);

    public CitadelSystem() {
        super(Family.all(CitadelComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CitadelComponent component = mapper.get(entity);
        Entity flag = new ItemWrapper(entity).getChild("flag").getEntity();
        TransformComponent transformComponent = ComponentRetriever.get(flag, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(flag, DimensionsComponent.class);

        if(component.state == -1) {
            component.state = 0;
            transformComponent.originX = dimensionsComponent.width/2f;
            transformComponent.originY = 0;
            transformComponent.scaleX = 0;
            transformComponent.scaleY = 0;
        }

        if(component.state == 1) {
            transformComponent.scaleX+=4f*deltaTime;
            transformComponent.scaleY+=4f*deltaTime;

            if(transformComponent.scaleX >= 1f || transformComponent.scaleX >= 1f) {
                transformComponent.scaleX=1f;
                transformComponent.scaleY=1f;
                component.state = 2;
                MusicMgr.getInstance().stop();
                SoundMgr.getInstance().play("Win");

                turnOffTurrets();
            }
        }

    }

    private void turnOffTurrets() {
        Family turretFamily = Family.all(TurretComponent.class).get();
        ImmutableArray<Entity> turrets = getEngine().getEntitiesFor(turretFamily);
        for(Entity turret: turrets) {
            TurretComponent component = turret.getComponent(TurretComponent.class);
            component.state = TurretComponent.TurretState.OFF;
        }
    }
}
