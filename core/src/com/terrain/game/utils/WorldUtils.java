package com.terrain.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cullycross on 3/30/15.
 */
public class WorldUtils {

    public static World createWorld() {
        return new World(Constants.WORLD_GRAVITY, true);
    }

    public static void drawHills(World world, int numberOfHills, int pixelStep) {
        Random rnd = new Random();
        float hillStartY = 140 + rnd.nextInt(201);
        final float hillWidth = Gdx.graphics.getWidth() / numberOfHills;
        final float hillSliceWidth = hillWidth / pixelStep;

        final float appHeight = Gdx.graphics.getHeight();

        List<Vector2> hillList;

        for(int i = 0; i < numberOfHills; i++) {
            int randomHeight = rnd.nextInt(101);

            if(i != 0) {
                hillStartY += randomHeight;
            }

            for(int j = 0; j < hillSliceWidth; j++) {
                hillList = new ArrayList<Vector2>();

                hillList.add(
                        new Vector2((j * pixelStep + hillWidth * i),
                                0f)
                );
                hillList.add(
                        new Vector2((j * pixelStep + hillWidth * i),
                                (float)(hillStartY - randomHeight * Math.cos(2 * Math.PI / hillSliceWidth * j)))
                );
                hillList.add(
                        new Vector2(((j + 1) * pixelStep + hillWidth * i),
                                (float)(hillStartY - randomHeight * Math.cos(2 * Math.PI / hillSliceWidth * (j + 1))))
                );
                hillList.add(
                        new Vector2(((j + 1) * pixelStep + hillWidth * i),
                                0f)
                );

                BodyDef sliceBody = new BodyDef();

                Vector2 centre = findCentroid(hillList);
                sliceBody.position.set(centre);

                for(int k = 0; k< hillList.size(); k++){
                    hillList.get(k).sub(centre);
                }

                PolygonShape slicePoly = new PolygonShape();

                Vector2 [] vertices = new Vector2[hillList.size()];
                vertices = hillList.toArray(vertices);
                slicePoly.set(vertices);

                FixtureDef sliceFixture = new FixtureDef();
                sliceFixture.shape = slicePoly;
                Body worldSlice = world.createBody(sliceBody);
                worldSlice.createFixture(sliceFixture);
            }
            hillStartY -= randomHeight;
        }

    }

    private static Vector2 findCentroid(List<Vector2> hill) {
        Vector2 c = new Vector2();

        float area = 0f;
        float p1X = 0f;
        float p1Y = 0f;

        float inv3 = 1f/3f;

        int length = hill.size();

        for (int i = 0; i < length; i++) {
            Vector2 p2 = hill.get(i);
            Vector2 p3 = i + 1 < length ? hill.get(i+1) : hill.get(0);
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
        c.x *= 1.0/area;
        c.y *= 1.0/area;
        return c;
    }
}
