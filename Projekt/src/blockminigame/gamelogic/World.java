package blockminigame.gamelogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import blockminigame.Game;
import blocks.Block;

public class World {
	
	private Block player;
	private List<Block> blocks;
	private Random random;
	private Vector2f gravity;
	private int sizeX;
	private int sizeY;
	
	public World(int sizeX, int sizeY) {
		this.blocks = new ArrayList<>();
		this.random = new Random();
		this.gravity = new Vector2f(0, 0);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public World(Vector2f gravity) {
		super();
		this.gravity = gravity;
	}
	
	public void addBlock(Block block) {
		blocks.add(block);
	}
	
	public void setGravity(Vector2f gravity) {
		this.gravity = gravity;
	}
	
	public boolean doBlockCollide(Block block, boolean simulation) {
		
		try {
			
			if (!block.isCollideable()) return false;
			
			for (Block b : blocks) {
				
				if (b.isCollideable()) {
					
					if (b.isCordInBlock(block.getPosX(), block.getPosY()) ||
						b.isCordInBlock(block.getPosX() + block.getWidth(), block.getPosY()) ||
						b.isCordInBlock(block.getPosX(), block.getPosY() + block.getHeight()) ||
						b.isCordInBlock(block.getPosX() + block.getWidth(), block.getPosY() + block.getHeight())) {
						
						if (!simulation && b != null) {
							
							boolean doPhysics1 = block.onCollide(this, b);
							boolean doPhysics2 = b.onCollide(this, block);
							
//							Vector2f v1 = new Vector2f(block.getPosX() + block.getWidth() / 2, block.getPosY() + block.getHeight() / 2);
//							Vector2f v2 = new Vector2f(b.getPosX() + b.getWidth() / 2, b.getPosY() + b.getHeight() / 2);
//							
//							Vector2f m1 = new Vector2f(block.getMotionX(), block.getMotionY());
//							Vector2f m2 = new Vector2f(b.getMotionX(), b.getMotionY());
//							
//							float dist12x = Math.max(v1.x, v2.x) - Math.min(v1.x, v2.x);
//							float dist12y = Math.max(v1.y, v2.y) - Math.min(v1.y, v2.y);
//							
//							float syncP1 = (Math.max(block.getHeight(), block.getWidth()) - Math.min(block.getHeight(),  block.getWidth())) / 2;
//							float syncP2 = (Math.max(b.getHeight(), b.getWidth()) - Math.min(b.getHeight(),  b.getWidth())) / 2;
//							
//							if (block.getHeight() > block.getWidth()) dist12y -= syncP1;
//							if (block.getWidth() > block.getHeight()) dist12x -= syncP1;
//							
//							if (b.getHeight() > b.getWidth()) dist12y -= syncP2;
//							if (b.getWidth() > b.getHeight()) dist12x -= syncP2;
//							
//							float mass1 = block.getHeight() * block.getWidth();
//							float mass2 = b.getHeight() * b.getWidth();
//							
//							System.out.println("--------------");
//
//							System.out.println("M in " + m1.x + " " + m2.x);
//							
//							float m1xg = m1.x < 0 ? m1.x * -1 : m1.x;
//							float m2xg = m2.x < 0 ? m2.x * -1 : m2.x;
//							
//							System.out.println("M2 in " + m1xg + " " + m2xg);
//							
//							float forceX1 = m1xg * mass1;
//							float forceX2 = m2xg * mass2;
//							
//							float resultForceX = (forceX1 + forceX2) / 2;
//							
//							System.out.println(forceX1 + " " + forceX2);
//							System.out.println("Result F " + resultForceX);
//							
//							if (dist12x < dist12y) resultForceX *= 0.5F;
//							
//							m1.x = resultForceX / mass1;
//							m2.x = resultForceX / mass2;
//							
//							if (m1.x < m2.x) {
//								m1.x = m1.x * -1;
//							} else {
//								m2.x = m2.x * -1;
//							}
//							
//							float m1yg = Float.parseFloat(new String("" + m1.y).replace("-", ""));
//							float m2yg = Float.parseFloat(new String("" + m2.y).replace("-", ""));
//							
//							float forceY1 = m1yg * mass1;
//							float forceY2 = m2yg * mass2;
//							
//							float resultForceY = (forceY1 + forceY2) / 2;
//							
//							if (dist12x > dist12y) resultForceY *= 0.5F;
//							
//							m1.y = resultForceY / mass1;
//							m2.y = resultForceY / mass2;
//							
//							if (m1.y < m2.y) {
//								m1.y = m1.y * -1;
//							} else {
//								m2.y = m2.y * -1;
//							}
//							
//							System.out.println("--------");
							
							Vector2f blockV = new Vector2f(block.getPosX() + block.getWidth() / 2, block.getPosY() + block.getHeight() / 2);
							Vector2f bV = new Vector2f(b.getPosX() + b.getWidth() / 2, b.getPosY() + b.getHeight() / 2);
							
							float mx = block.getMotionX();
							float my = block.getMotionY();
							float mx2 = b.getMotionX();
							float my2 = b.getMotionY();
							
							float f1x = blockV.x - bV.x;
							float f1y = blockV.y - bV.y;
							
							float f1xg = f1x < 0 ? -f1y : f1x;
							float f1yg = f1y < 0 ? -f1y : f1y;
							
							float syncP = (Math.max(block.getHeight(), block.getWidth()) - Math.min(block.getHeight(),  block.getWidth())) / 2;
							float syncP2 = (Math.max(b.getHeight(), b.getWidth()) - Math.min(b.getHeight(),  b.getWidth())) / 2;
							
							if (block.getHeight() > block.getWidth()) f1yg -= syncP;
							if (block.getWidth() > block.getHeight()) f1xg -= syncP;
							
							if (b.getHeight() > b.getWidth()) f1yg -= syncP2;
							if (b.getWidth() > b.getHeight()) f1xg -= syncP2;
							
							float f3 = (1F - Game.WIGHT) * -1;
							
							if (f1xg - f1yg > 0.1F) {
								
								mx *= -1;
								mx2 = mx * f3;
								my2 += mx * Game.WIGHT;
								
							} else if (f1yg - f1xg > 0.1F) {
								
								my *= -1;
								my2 = my * f3;
								mx2 += mx * Game.WIGHT;
								
							}
							
							if (doPhysics1) {
								block.setMotion(mx, my);
								block.update(1F, random, gravity);
							}
							if (doPhysics2) {
								b.setMotion(mx2, my2);
								b.update(1F, random, gravity);
							}
							
						}
						
						return true;
						
					}
				
				}
				
			}
			
			
		} catch (NullPointerException e) {};
		
		return false;
		
	}
	
	public void update(float speed) {
		
		for (int i = 0; i < blocks.size(); i++) {
			
			Block block = blocks.get(i);
			block.update(speed, random, gravity);
			doBlockCollide(block, false);
			
			if (!block.isBlockExisting()) {
				blocks.remove(i);
				i--;
			}
			
		}
		
	}
	
	public void render() {
		
		for (Block block : blocks) {
			
			block.render();
			
		}
		
	}
	
	public List<Block> getBlocks() {
		return blocks;
	}
	
	public static void loadWorld(File file) {
		
		try {
			
			FileInputStream is = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Gson gson = new Gson();
			JsonObject main = gson.fromJson(br, JsonObject.class);
			br.close();
			is.close();
			
			JsonArray gravity = main.get("gravity").getAsJsonArray();
			Vector2f level_gravity = new Vector2f(gravity.get(0).getAsFloat(), gravity.get(1).getAsFloat());
			
			JsonArray size = main.get("size").getAsJsonArray();
			Vector2f level_size = new Vector2f(size.get(0).getAsFloat(), size.get(1).getAsFloat());
			
			World world = new World((int) level_size.x, (int) level_size.y);
			world.setGravity(level_gravity);
			
			JsonArray blocks = main.get("blocks").getAsJsonArray();
			for (int i = 0; i < blocks.size(); i++) {
				
				JsonObject block = blocks.get(i).getAsJsonObject();
				
				// TODO
				
				world.addBlock(null);
				
			}
			
			Game.getInstance().setWorld(world);
			
		} catch (Exception e) {
			System.err.println("Can not load World from " + file);
			e.printStackTrace();
		}
		 
	}
	
	public void saveWorld(File file) {
		

		try {
			
			Gson gson = new Gson();
			
			JsonObject main = new JsonObject();
			
			JsonArray blocks = new JsonArray();
			
			for (Block block2 : this.blocks) {
				
				JsonObject block = new JsonObject();
				
				// TODO
				
				blocks.add(block);
				
			}
			
			main.add("blocks", blocks);
			
			JsonArray gravity = new JsonArray();
			gravity.add(this.gravity.getX());
			gravity.add(this.gravity.getY());
			
			main.add("gravity", gravity);
			
			JsonArray size = new JsonArray();
			size.add(this.sizeX);
			size.add(this.sizeY);
			
			main.add("size", size);
			
			FileOutputStream os = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(main);
			bw.write(jsonString);
			bw.close();
			os.close();
			
		} catch (Exception e) {
			System.err.println("Can not load World from " + file);
			e.printStackTrace();
		}
		
	}
	
	public Block getPlayer() {
		return player;
	}
	
	public void setPlayer(Block player) {
		this.player = player;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public Vector2f getGravity() {
		return gravity;
	}
	
}
