package com.underwater.thm;

import com.badlogic.ashley.core.Component;

/**
 * Created by CyberJoe on 8/23/2015.
 */
public class CitadelComponent implements Component {

    // 0 = not rised, 1 - in progress , 2- rised;
    public int state = -1;

    public void riseFlag() {
        if(state == 0) {
            state = 1;
        }
    }
}
