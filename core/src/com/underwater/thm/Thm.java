package com.underwater.thm;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by azakhary on 8/22/2015.
 */
public class Thm extends ApplicationAdapter {

    private Viewport viewport;
    private SceneLoader sl;
    private NavigationMap navigationMap;

    private ItemWrapper root;
    private TiledWorld tiledWorld;

    private int currLevelNum;

    private boolean markForNewLevel = false;

    private SpawnSystem spawnSystem;

    private Preferences prefs;

    private Image menuImg;
    private boolean isMenu = true;

    @Override
    public void create () {
        prefs = Gdx.app.getPreferences("thm_save_data");
        Integer lastLevel = prefs.getInteger("level", 1);
        currLevelNum = lastLevel;
        currLevelNum=3;

        SoundMgr.getInstance();
        MusicMgr.getInstance();

        viewport = new StretchViewport(450, 300);
        navigationMap = new NavigationMap();
        sl = new SceneLoader();

        MinionSystem minionSystem = new MinionSystem(sl, navigationMap);

        spawnSystem = new SpawnSystem(this, viewport, sl, navigationMap);

        sl.getEngine().addSystem(minionSystem);
        sl.getEngine().addSystem(spawnSystem);
        sl.getEngine().addSystem(new TurretSystem(sl));
        sl.getEngine().addSystem(new PortalSystem(sl, navigationMap));
        sl.getEngine().addSystem(new CitadelSystem());
        sl.getEngine().addSystem(new BulletSystem());
        sl.getEngine().addSystem(new DisposingParticleSystem());

        loadNextLevel();

        menuImg = new Image(new Texture(Gdx.files.internal("menu.png")));
    }

    public void initLevel(int level) {
        prefs.putInteger("level", level);
        prefs.flush();

        sl.loadScene("level"+level, viewport);
        root = new ItemWrapper(sl.getRoot());
        tiledWorld = new TiledWorld(sl.getEngine(), 19, 12, 25);
        navigationMap.init(tiledWorld);
        sl.addComponentsByTagName("turret", TurretComponent.class);
        sl.addComponentsByTagName("portal_out", PortalComponent.class);
        sl.addComponentsByTagName("portal_in", PortalComponent.class);
        sl.addComponentsByTagName("citadel", CitadelComponent.class);
        // assign portal targets
        PortalSystem.preProcessPortals(sl);
        spawnSystem.setMinions(30);

        MusicMgr.getInstance().play();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0 ,0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sl.getEngine().update(Gdx.graphics.getDeltaTime());

        if(markForNewLevel && sl.getEngine().getEntities().size() == 0) {
            initLevel(currLevelNum);
            markForNewLevel = false;
        }

        if(isMenu) {
            sl.getBatch().begin();
            menuImg.setScale(1f / 4f);
            menuImg.draw(sl.getBatch(), 1f);
            sl.getBatch().end();

            if(Gdx.input.justTouched()) {
                isMenu = false;
                SoundMgr.getInstance().play("Bell");
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void checkIfWin() {
        if(root == null) return;
        CitadelComponent citadelComponent = root.getChild("citadel").getEntity().getComponent(CitadelComponent.class);
        if(citadelComponent != null) {
            if (citadelComponent.state == 2) {
                currLevelNum++;
                loadNextLevel();
            }
        }
    }

    private void loadNextLevel() {
        if(currLevelNum > 5) currLevelNum = 1;
        sl.getEngine().removeAllEntities();
        markForNewLevel = true;
    }
}
