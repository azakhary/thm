package com.underwater.thm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;

/**
 * Created by azakhary on 8/22/2015.
 */
public class PortalComponent implements Component {
    public int portalId = -1;

    public boolean isIn = false;

    public Entity target = null;

    public float coolDown;

    public float currentCoolDown;

    public void load(MainItemComponent mainItemComponent) {
        CustomVariables customVariables = new CustomVariables();
        customVariables.loadFromString(mainItemComponent.customVars);

        portalId = customVariables.getIntegerVariable("portal_id");
        if(mainItemComponent.tags.contains("portal_in")) {
            isIn = true;
            coolDown = customVariables.getFloatVariable("interval");
        }
        currentCoolDown = coolDown;
    }
}
