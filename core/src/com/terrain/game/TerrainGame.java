package com.terrain.game;

import com.badlogic.gdx.Game;
import com.terrain.game.screens.GameScreen;

public class TerrainGame extends Game {
	
	@Override
	public void create () {

        setScreen(new GameScreen());
	}
}
