package com.underwater.thm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

/**
 * Created by azakhary on 8/23/2015.
 */
public class SoundMgr {

    private static SoundMgr instance;

    public HashMap<String, Sound> fx = new HashMap<String, Sound>();

    private SoundMgr() {
        loadSound("MonkSpawn");
        loadSound("MonkDie");
        loadSound("Shot1");
        loadSound("Shot2");
        loadSound("Shot3");
        loadSound("Shot4");
        loadSound("Shot5");
        loadSound("Win");
        loadSound("Bell");
    }

    private void loadSound(String name) {
        fx.put(name, Gdx.audio.newSound(Gdx.files.internal("sound/"+name+".ogg")));
    }

    public static SoundMgr getInstance() {
        if(instance == null) {
            instance = new SoundMgr();
        }

        return instance;
    }

    public void play(String name) {
        fx.get(name).play();
    }

    public void play(String name, float volume) {
        long id = fx.get(name).play();
        fx.get(name).setVolume(id, volume);
    }
}
