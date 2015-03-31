package com.terrain.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.terrain.game.utils.WorldUtils;

/**
 * Created by cullycross on 3/30/15.
 */
public class GameStage extends Stage {

    private World mWorld;

    private OrthographicCamera mCamera;
    private Box2DDebugRenderer mRenderer;

    @Override
    public void draw() {
        super.draw();
        mRenderer.render(mWorld, mCamera.combined);
    }

    public GameStage() {

        mWorld = WorldUtils.createWorld();
        mRenderer = new Box2DDebugRenderer();

        mCamera = new OrthographicCamera(800, 480);
        mCamera.position.set(mCamera.viewportWidth / 2, mCamera.viewportHeight / 2, 0f);
        mCamera.update();
        WorldUtils.drawHills(mWorld, 2, 10);
    }
}
