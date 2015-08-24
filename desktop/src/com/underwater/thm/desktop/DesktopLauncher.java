package com.underwater.thm.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.underwater.thm.Thm;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "The Holly Matter";
        config.width = 960;
        config.height= 600;
        //config.fullscreen = true;
		new LwjglApplication(new Thm(), config);
	}
}
