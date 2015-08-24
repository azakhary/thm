package com.underwater.thm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Created by azakhary on 8/23/2015.
 */
public class MusicMgr {

    private static MusicMgr instance;

    private Music music;

    private MusicMgr() {
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/bgmusic.ogg"));
        music.setLooping(true);
        music.play();
    }

    public static MusicMgr getInstance() {
        if(instance == null) {
            instance = new MusicMgr();
        }

        return instance;
    }

    public void play() {
        if(!music.isPlaying()) {
            music.play();
        }
    }

    public void stop() {
        music.stop();
    }


}
