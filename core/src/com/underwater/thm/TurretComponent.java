package com.underwater.thm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Created by azakhary on 8/22/2015.
 */
public class TurretComponent implements Component {

    public int power = 2;
    public float radius = 60;
    public float rotationSpeed = 400;

    public float coolDown = 0;
    public float timeIdle = 0;

    public TurretState state = TurretState.NONE;
    public Entity target;

    enum TurretState {
        NONE, SEARCHING, LOCKED, OFF
    }

}
