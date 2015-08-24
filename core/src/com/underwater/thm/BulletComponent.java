package com.underwater.thm;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by CyberJoe on 8/23/2015.
 */
public class BulletComponent implements Component {

    private float angle;
    public Vector2 to;

    public float speed = 300f;

    public BulletComponent(Vector2 from, Vector2 to) {
        Vector2 diff = new Vector2(to);
        diff.sub(from);
        angle = diff.angle();

        this.to = to;
    }

    public float getAngle() {
        return angle;
    }
}
