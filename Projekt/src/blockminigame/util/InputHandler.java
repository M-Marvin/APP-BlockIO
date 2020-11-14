package blockminigame.util;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import blockminigame.Game;
import blockminigame.gamelogic.World;
import blocks.Block;

public class InputHandler {
	
	private static HashMap<Integer, Boolean> pressedKeys = new HashMap<>();
	
	public static void handleInput() {
		
		Game game = Game.getInstance();
		World world = game.getWorld();
		Block player = world != null ? world.getPlayer() : null;
		
		if (isKeyPressed(Keyboard.KEY_LSHIFT)) {
			game.getGameOverlay().elapsedTime = 0;
			game.getGameOverlay().level = 1;
			game.getGameOverlay().levelVariant = 0;
			game.reGen();
		} else if (isKeyPressed(Keyboard.KEY_ESCAPE)) {
			game.losGame();
		}
		
		if (player != null) {
			
			float mx = player.getMotionX();
			float my = player.getMotionY();
			
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				my -= 0.1F;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				my += 0.1F;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				mx -= 0.1F;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				mx += 0.1F;
			}
			
			player.setMotion(mx, my);
			
		}
		
	}
	
	public static boolean isKeyPressed(int key) {
		boolean result = Keyboard.isKeyDown(key) && !pressedKeys.get(key);
		pressedKeys.put(key, Keyboard.isKeyDown(key));
		return result;
	}
	
}
