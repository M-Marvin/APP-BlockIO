package blockminigame;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import blockminigame.gamelogic.BlockProvoiderRegistry;
import blockminigame.gamelogic.World;
import blockminigame.gamelogic.WorldGenerator;
import blockminigame.render.GameOverlay;
import blockminigame.render.GameOverlay.EnumGameInfoMode;
import blockminigame.render.TextureIO;
import blockminigame.util.InputHandler;
import blockminigame.util.SoundPlayer;
import blockminigame.util.TextureLoader;
import blocks.BlockDefault;
import blocks.BlockDefaultProvoider;
import blocks.BlockGoal;
import blocks.BlockGoalProvoider;

public class Game {
	
	//SETTINGS
	public static final int ZOOM_SPEED = 2;
	public static final Color BACKGROUND_COLOR = new Color(0, 0, 200);
	public static final int SIZE_X = 1000;
	public static final int SIZE_Y = 600;
	public static final float GRAVITY = 0.99F;
	public static final float WIGHT = 0.1F;
	
	private File systemPath;
	private File resourceFolder;
	private boolean devMode;
	private World world;
	private int FPS;
	private int countedFPS;
	private int lastFPS;
	private long lastFrame;
	private static Game instance;
	private GameOverlay gameOverlay;
	public int curentWorldZoomX;
	public int curentWorldZoomY;
	
	//Main-Function (Main-Constructor)
	public static void main(String[] args) {
		
		Game game = new Game();
		game.start();
		
	}
	
	//Functions
    public void updateFPS() {
    	
        if (getTime() - lastFPS > 1000) {
        	FPS = countedFPS;
        	countedFPS = 0;
            lastFPS += 1000;
        }
        countedFPS++;
    }
	
	public void start() {
		
		Game.instance = this;
		
		try {
			
			systemPath = new File(ClassLoader.getSystemResource("").getPath().substring(1).replace("%20", " "));
			resourceFolder = new File(systemPath, "res/");
			devMode = !resourceFolder.exists() || !resourceFolder.isDirectory();
			if (devMode) resourceFolder = new File(systemPath.getParentFile(), "res/");
			File libpath = new File(resourceFolder, "native/");
			
			System.out.println("System Path: " + libpath);
			System.setProperty("java.library.path", libpath.getAbsolutePath());
			Field field = ClassLoader.class.getDeclaredField("sys_paths");
			field.setAccessible(true);
			field.set(null, null);
			
			Display.setResizable(true);
			Display.setDisplayMode(new DisplayMode(SIZE_X, SIZE_Y));
			Display.setInitialBackground(BACKGROUND_COLOR.getRed() / 255F, BACKGROUND_COLOR.getGreen() / 255F, BACKGROUND_COLOR.getBlue() / 255F);
			Display.create();
			
		} catch (Exception e) {
			System.err.println("ERROR: Cant instanziate Game!");
			e.printStackTrace();
			System.exit(-1);
		}
		
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        
	    GL11.glOrtho(0.0f, Display.getWidth(), Display.getHeight(), 0.0f, 0.0f, 1.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		System.out.println("Configure GLStates");
        
		lastFPS = (int) getTime() - 1000;
		gameOverlay = new GameOverlay();
		
		// Register Block Provoider
		BlockProvoiderRegistry.getRegistry().registerBlockProvoider(new BlockDefaultProvoider(), BlockDefault.class);
		BlockProvoiderRegistry.getRegistry().registerBlockProvoider(new BlockGoalProvoider(), BlockGoal.class);
		
		while (!Display.isCloseRequested()) {
			
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			if (Display.wasResized()) GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			
			int delta = getDelta();
			updateFPS();
			
			gameTick(delta);
			renderTick();

			Display.sync(60);
			Display.update();
			
		}
		
		Display.destroy();
		
	}
	
	public void winGame() {
		
		gameOverlay.level++;
		world = null;
		
		if (gameOverlay.level > gameOverlay.score) {
			gameOverlay.score = gameOverlay.level - 1;
		}
		
		reGen();
		
		gameOverlay.invulnereableTimer = gameOverlay.level > 25 ? gameOverlay.level > 50 ? 250 : 150 : 0;
		
	}
	
	public void reGen() {
		
		WorldGenerator worldGen = new WorldGenerator(gameOverlay.level, 1010324535L);
		worldGen.genWorld(gameOverlay.level);
		this.world = worldGen.getWorld();
		
	}
	
	public void losGame() {
		
		world = null;
		
		gameOverlay.levelVariant = 0;
		gameOverlay.level = 0;
		
	}
	
	public void gameTick(int delta) {
		
		Display.setTitle("BlockMinigame | FPS: " + FPS);
		
		InputHandler.handleInput();
//		EditingModeHandler.handleEditiingMode();
		SoundPlayer.update();
		
		float speed = (float) delta / (float) 16;
		
		if (world != null) {
			
			if (gameOverlay.invulnereableTimer > 0) gameOverlay.invulnereableTimer--;
			
			world.update(speed);
			
			if (world.getBlocks().contains(world.getPlayer())) {
				this.gameOverlay.removeInfo(EnumGameInfoMode.LOS_INFO);
				this.gameOverlay.removeInfo(EnumGameInfoMode.START_INFO);
				gameOverlay.elapsedTime += 20 * speed;
			} else if (!world.getBlocks().contains(world.getPlayer())) {
				this.gameOverlay.addInfo(EnumGameInfoMode.LOS_INFO);
//				goal.setWall(true);
//				goal.setPosition(-100, -100);
//				for (Block b : world.getBlocks().toArray(new Block[] {})) {
//					if (b != this.getGoal() && b.getScale() == -1) {
//						b.setExploding(true);
//						if (rand.nextInt(1000) == 0) b.onCollide(world, b);
//					}
//				}
			}

			if (world.getPlayer() != null) {
				
				float rDistX = curentWorldZoomX + world.getPlayer().getPosX() - SIZE_X / 2;
				float rDistY = curentWorldZoomY + world.getPlayer().getPosY() - SIZE_Y / 2;
				
				if (rDistX > SIZE_X / 4) {
					curentWorldZoomX -= ZOOM_SPEED;
				} else if (rDistX < -SIZE_X / 4) {
					curentWorldZoomX += ZOOM_SPEED;
				}
				
				if (rDistY > SIZE_Y / 4) {
					curentWorldZoomY -= ZOOM_SPEED;
				} else if (rDistY < -SIZE_Y / 4) {
					curentWorldZoomY += ZOOM_SPEED;
				}
				
			}
			
		} else {
			
			this.gameOverlay.removeInfo(EnumGameInfoMode.LOS_INFO);
			this.gameOverlay.addInfo(EnumGameInfoMode.START_INFO);
			
		}
		
	}
	
	public void renderTick() {
		
		if (world != null) {
			
			GL11.glPushMatrix();
			{
				
				
				GL11.glTranslatef(curentWorldZoomX, curentWorldZoomY, 0);
				
				TextureIO background = TextureLoader.loadTexture("background.png");
				background.bind();
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0F, 1F);
				GL11.glVertex2f(0, this.world.getSizeY());
				GL11.glTexCoord2f(1F, 1F);
				GL11.glVertex2f(this.world.getSizeX(), this.world.getSizeY());
				GL11.glTexCoord2f(1F, 0F);
				GL11.glVertex2f(this.world.getSizeX(), 0);
				GL11.glTexCoord2f(0F, 0F);
				GL11.glVertex2f(0, 0);
				GL11.glEnd();
				background.unbind();
				
				world.render();
				
			}
			GL11.glPopMatrix();
			
		}
		
		gameOverlay.render();
		
	}
	
	//Getter
	public File getResourceFolder() {
		return resourceFolder;
	}
		
	public GameOverlay getGameOverlay() {
		return gameOverlay;
	}

    public World getWorld() {
		return world;
	}
    
	public static Game getInstance() {
		return instance;
	}
		
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;
      
        return delta;
    }
    
    //Setter
	public void setWorld(World world) {
		this.world = world;
	}
	
}
