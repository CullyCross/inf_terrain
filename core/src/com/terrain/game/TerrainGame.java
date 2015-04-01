package com.terrain.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerrainGame extends Game {

    Box2DDebugRenderer mRenderer;
    OrthographicCamera mCamera;
    World mWorld;
    Random mRandom;

    float mNextHill;

    int mCountOfSlices;

    final static int PIXEL_STEP = 10;
	
	@Override
	public void create () {
        mWorld = new World(new Vector2(0, -10), true);
        mRenderer = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mCamera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0f);
        mCamera.update();

        mRandom = new Random();
        mNextHill = 140 + mRandom.nextFloat() * 200;
        mCountOfSlices = 0;

        mNextHill = drawHill(PIXEL_STEP, 0, mNextHill);
	}

    @Override
    public void render() {
        super.render();

        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mWorld.step(1 / 30, 10, 10);
        mWorld.clearForces();

        Array<Body> bodies = new Array<Body>();
        mWorld.getBodies(bodies);
        for (Body currentBody : bodies) {

            currentBody.setTransform(currentBody.getPosition().x - 1, currentBody.getPosition().y, 0);

            if (currentBody.getPosition().x <= 0) {
                int sliceWidth = Gdx.graphics.getWidth() / PIXEL_STEP;
                if(mCountOfSlices <= sliceWidth + 1) {
                    mNextHill = drawHill(PIXEL_STEP,
                            Gdx.graphics.getWidth(),
                            mNextHill);
                }
                mWorld.destroyBody(currentBody);
                mCountOfSlices--;
            }
        }

        mRenderer.render(mWorld, mCamera.combined);
    }

    private float drawHill(int pixelStep, float xOffset, float yOffset) {
        float hillStartY = yOffset;
        final float hillWidth = Gdx.graphics.getWidth();
        final float hillSliceWidth = hillWidth / pixelStep;
        mCountOfSlices += hillSliceWidth;

        List<Vector2> hillVector;
        final float randomHeight = mRandom.nextFloat() * 100;

        if (xOffset != 0) {
            hillStartY += randomHeight;
        }
        for (int j = 0; j < hillSliceWidth; j++) {
            hillVector=new ArrayList<Vector2>();

            hillVector.add(new Vector2((j * pixelStep + xOffset), 0f));
            hillVector.add(new Vector2((j * pixelStep + xOffset),
                    (float)(hillStartY - randomHeight * Math.cos(2 * Math.PI / hillSliceWidth * j))));
            hillVector.add(new Vector2(((j + 1) * pixelStep + xOffset),
                    (float)(hillStartY - randomHeight * Math.cos(2 * Math.PI / hillSliceWidth * (j + 1)))));
            hillVector.add(new Vector2(((j + 1) * pixelStep + xOffset), 0f));

            BodyDef sliceBody = new BodyDef();
            Vector2 centre = findCentroid(hillVector);
            sliceBody.position.set(centre.x, centre.y);
            for (int z = 0; z < hillVector.size(); z++) {
                hillVector.get(z).sub(centre);
            }
            PolygonShape slicePoly = new PolygonShape();

            slicePoly.set(hillVector.toArray(new Vector2[hillVector.size()]));
            FixtureDef sliceFixture = new FixtureDef();
            sliceFixture.shape = slicePoly;
            Body worldSlice = mWorld.createBody(sliceBody);
            worldSlice.createFixture(sliceFixture);
        }
        hillStartY -= randomHeight;
        return (hillStartY);
    }

    private Vector2 findCentroid(List<Vector2> vs) {
        Vector2 c = new Vector2();
        float area = 0.0f;
        float p1X = 0.0f;
        float p1Y = 0.0f;
        float inv3 = 1.0f/3.0f;

        int length = vs.size();

        for (int i = 0; i < length; ++i) {
            Vector2 p2 = vs.get(i);
            Vector2 p3 = i + 1 < length ? vs.get(i+1) : vs.get(0);
            float e1X = p2.x - p1X;
            float e1Y = p2.y - p1Y;
            float e2X = p3.x - p1X;
            float e2Y = p3.y - p1Y;
            float D = (e1X * e2Y - e1Y * e2X);
            float triangleArea = 0.5f * D;
            area += triangleArea;
            c.x += triangleArea * inv3 * (p1X + p2.x + p3.x);
            c.y += triangleArea * inv3 * (p1Y + p2.y + p3.y);
        }
        c.x *= 1.0 / area;
        c.y *= 1.0 / area;
        return c;
    }
}
