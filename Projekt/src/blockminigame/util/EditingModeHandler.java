package blockminigame.util;

import java.awt.Color;
import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import blockminigame.Game;
import blocks.BlockDefault;

public class EditingModeHandler {
	
	//Editing Mode variables
	//	isWall	|	isExploding	|	isColorChanger	|	isRandomMoving	//
	private static final boolean[][] BLOCK_STATES = new boolean[][] {new boolean[] {false, false, false, false}, new boolean[] {true, false, false, false}, new boolean[] {false, true, false, false}, new boolean[] {false, false, true, false}, new boolean[] {true, false, false, true}, new boolean[] {false, true, true, false}, new boolean[] {false, false, true, true}};
	private static int createColor;
	private static int changeState;
	private static Vector2f createFirstPoint;
	private static boolean hasStateChanged;
	private static BlockDefault clickedBlock;
	
	public static void handleEditiingMode() {
		
		Game game = Game.getInstance();
		
		//Editing Mode handling
		if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
			
			if (game.getWorld() != null) {

				float x = ((float) Mouse.getX() / Display.getWidth() * Game.SIZE_X - game.curentWorldZoomX);
				float y = ((((float) Display.getHeight() - (float) Mouse.getY()) / Display.getHeight()) * Game.SIZE_Y - game.curentWorldZoomY);
				
				if (Mouse.isButtonDown(2) && !hasStateChanged) {
					
					for (BlockDefault block : game.getWorld().getBlocks()) {
						
						if (block.isCordInBlock(x, y)) {
							
							changeState++;
							if (changeState > BLOCK_STATES.length - 1) changeState = 0;
							
							boolean[] state = BLOCK_STATES[changeState];
							
							block.setWall(state[0]);
							block.setExploding(state[1]);
							block.setColorChanger(state[2]);
							block.setRandomMoving(state[3]);
							
							if (!block.isWall()) block.setColor(Game.BLOCK_COLORS[createColor]);
							
						}
						
						hasStateChanged = true;
						
					}
					
				} else if (!Mouse.isButtonDown(2)) {
					
					hasStateChanged = false;
					
				}
				
				float mw = Mouse.getDWheel();
				
				if (Mouse.isButtonDown(0) && mw == 0) {
					
					for (BlockDefault block : game.getWorld().getBlocks()) {
						
						if (block.isCordInBlock(x, y)) {
							
							clickedBlock = block;
							clickedBlock.setMotion(Mouse.getDX(), Mouse.getDY() * -1);
							
						}
						
					}
					
				} else if (Mouse.isButtonDown(0) && mw != 0) {
					
					System.out.println("Fdfsfhshf");
					clickedBlock.setScale(1);
					
				} else {
					
					clickedBlock = null;
					
				}
				
				if (Mouse.isButtonDown(1) && createFirstPoint == null) {
					
					createFirstPoint = new Vector2f(x, y);
					
				} else if (!Mouse.isButtonDown(1) && createFirstPoint != null) {
					
					float fromX = Math.min(createFirstPoint.x, x);
					float fromY = Math.min(createFirstPoint.y, y);
					float toX = Math.max(createFirstPoint.x, x);
					float toY = Math.max(createFirstPoint.y, y);
					float width = toX - fromX;
					float height = toY - fromY;
					Color color = Game.BLOCK_COLORS[createColor];
					
					if (width > 5 && height > 5) {
						
						BlockDefault newBlock = new BlockDefault(fromX, fromY, width, height, color, Game.getInstance().getBlockTextureMap());
						game.getWorld().addBlock(newBlock);
						
					}
					
					createFirstPoint = null;
					
				} else if (createFirstPoint != null) {

					if (mw > 0) createColor++;
					if (createColor > Game.BLOCK_COLORS.length - 1) createColor = 0;
					
					float fromX = Math.min(createFirstPoint.x, x);
					float fromY = Math.min(createFirstPoint.y, y);
					float toX = Math.max(createFirstPoint.x, x);
					float toY = Math.max(createFirstPoint.y, y);
					float width = toX - fromX;
					float height = toY - fromY;
					Color color = Game.BLOCK_COLORS[createColor];
					
					if (width > 5 && height > 5) {
						
						GL11.glPushMatrix();
						{
							
							GL11.glTranslatef(game.curentWorldZoomX, game.curentWorldZoomY, 0);

							BlockDefault newBlock = new BlockDefault(fromX, fromY, width, height, color, Game.getInstance().getBlockTextureMap());
							newBlock.render();
							
						}
						GL11.glPopMatrix();
						
					}
					
				}
				
			}
			
			float changeAmount = 0.02F;
			if (InputHandler.isKeyPressed(Keyboard.KEY_NUMPAD4)) {
				Vector2f gravity = game.getWorld().getGravity();
				gravity.x -= changeAmount;
				game.getWorld().setGravity(gravity);
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_NUMPAD6)) {
				Vector2f gravity = game.getWorld().getGravity();
				gravity.x += changeAmount;
				game.getWorld().setGravity(gravity);
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_NUMPAD2)) {
				Vector2f gravity = game.getWorld().getGravity();
				gravity.y += changeAmount;
				game.getWorld().setGravity(gravity);
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_NUMPAD8)) {
				Vector2f gravity = game.getWorld().getGravity();
				gravity.y -= changeAmount;
				game.getWorld().setGravity(gravity);
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_NUMPAD5)) {
				game.getWorld().setGravity(new Vector2f(0, 0));
			}
			
			if (InputHandler.isKeyPressed(Keyboard.KEY_PRIOR)) {
				game.getGameOverlay().level++;
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_NEXT)) {
				if (game.getGameOverlay().level > 1) game.getGameOverlay().level--;
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_HOME)) {
				if (game.getGameOverlay().level != 0) game.getGameOverlay().level--;
				game.winGame();
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_DELETE)) {
				if (game.getGameOverlay().levelVariant != 0) {
					File levelFolder = new File(game.getResourceFolder(), "/levels/l_" + game.getGameOverlay().level);
					File[] levels = levelFolder.listFiles();
					levels[game.getGameOverlay().levelVariant - 1].delete();
					if (game.getGameOverlay().level != 0) game.getGameOverlay().level--;
					game.winGame();
					System.out.println("Delete Level " + game.getGameOverlay().level + " Variant " + game.getGameOverlay().levelVariant);
					if (levelFolder.list().length == 0) levelFolder.delete();
				}
			} else if (InputHandler.isKeyPressed(Keyboard.KEY_INSERT)) {
				File levelFolder = new File(game.getResourceFolder(), "/levels/l_" + game.getGameOverlay().level);
				if (!levelFolder.exists() || !levelFolder.isDirectory()) levelFolder.mkdir();
				File[] levels = levelFolder.listFiles();
				for (int i = 1; i <= levels.length + 1; i++) {
					File file = new File(levelFolder, "variant_" + i + ".json");
					if (!file.exists()) {
						game.getWorld().saveWorld(file);
						System.out.println("Saved Level " + game.getGameOverlay().level + " as new File " + file.getName());
						break;
					}
				}
			}
			
		}
		
	}
	
}
