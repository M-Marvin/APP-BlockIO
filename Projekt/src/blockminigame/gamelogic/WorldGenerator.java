package blockminigame.gamelogic;

import java.awt.Color;
import java.util.Random;

import blockminigame.Game;
import blockminigame.gamelogic.BlockProvoiderRegistry.BlockProvoider;
import blocks.Block;
import blocks.BlockDefault;
import blocks.BlockDefaultProvoider;
import blocks.BlockGoal;

public class WorldGenerator {
	
	public static final int SIZE_PER_LEVEL_X = 30;
	public static final int SIZE_PER_LEVEL_Y = 20;
	
	private World world;
	private Random rand;
	
	public WorldGenerator(World world, long seed) {
		this.world = world;
		this.world = world;
		this.rand = new Random(seed);
	}
	
	public WorldGenerator(int level, long seed) {
		
		this.rand = new Random(seed);
		
		int sizeX = rand.nextInt(level * SIZE_PER_LEVEL_X) + Game.SIZE_X;
		int sizeY = rand.nextInt(level * SIZE_PER_LEVEL_Y) + Game.SIZE_Y;
		
		this.world = new World(sizeX, sizeY);
		
	}
	
	@SuppressWarnings("rawtypes")
	public void genWorld(int level) {

		System.out.println("Gen World at Size " + world.getSizeX() + " " + world.getSizeY());
		
		BlockProvoider[] provoiders = BlockProvoiderRegistry.getRegistry().getBlockProvoiders();
				
		for (int i = 0; i < level * 2; i++) {
			
			BlockProvoider provoider = null;
			
			if (i == 0) {
				
				provoider = BlockProvoiderRegistry.getRegistry().getProvoider(BlockGoal.class);
				
			} else {
				
				for (BlockProvoider p : provoiders) {
					
					int chance = p.getChance();
					
					if (rand.nextInt(100) >= chance) {
						
						provoider = p;
						break;
						
					}
					
				}
				
				if (provoider == null) {

					i--;
					continue;
					
				}
				
			}
			
			Block block = provoider.provoideBlock(this.world, this.rand);

			float dist_x = block.getWidth() / 2 + 20 + 20;
			float dist_y = block.getHeight() / 2 + 20 + 20;
			
			int x = (int) (rand.nextInt((int) (this.world.getSizeX() - dist_x * 2)) + dist_x);
			int y = (int) (rand.nextInt((int) (this.world.getSizeY() - dist_y * 2)) + dist_y);
			
			block.setPosition(x, y);
			
			boolean flag = world.doBlockCollide(block, true);
			
			if (!flag) {
				
				for (Block b : world.getBlocks()) {
					
					if (	block.isCordInBlock(b.getPosX(), b.getPosY()) ||
							block.isCordInBlock(b.getPosX() + b.getWidth(), b.getPosY()) ||
							block.isCordInBlock(b.getPosX(), b.getPosY() + b.getHeight()) ||
							block.isCordInBlock(b.getPosX() + b.getWidth(), b.getPosY() + b.getHeight())) {
						
						flag = true;
						break;
						
					}
					
				}
				
			}
			
			if (flag) {
				
				i--;
				
			} else {
				
				world.addBlock(block);
				
			}
						
		}
		
		BlockDefaultProvoider bdp = (BlockDefaultProvoider) BlockProvoiderRegistry.getRegistry().getProvoider(BlockDefault.class);
		BlockDefault border1 = new BlockDefault(0, 0, this.world.getSizeX(), 20, null, bdp.getRandomBlockTextureMap(rand));
		border1.setWall(true);
		BlockDefault border2 = new BlockDefault(0, 0, 20, this.world.getSizeY(), null, bdp.getRandomBlockTextureMap(rand));
		border2.setWall(true);
		BlockDefault border3 = new BlockDefault(this.world.getSizeX() - 20, 0, 20, this.world.getSizeY(), null, bdp.getRandomBlockTextureMap(rand));
		border3.setWall(true);
		BlockDefault border4 = new BlockDefault(0, this.world.getSizeY() - 20, this.world.getSizeX(), 20, null, bdp.getRandomBlockTextureMap(rand));
		border4.setWall(true);
		world.addBlock(border1);
		world.addBlock(border2);
		world.addBlock(border3);
		world.addBlock(border4);
		
		int width = rand.nextInt(20) + 20;
		int height = rand.nextInt(20) + 20;
		float dist_x = width / 2 + 20 + 20;
		float dist_y = height / 2 + 20 + 20;
		int x = (int) (rand.nextInt((int) (this.world.getSizeX() - dist_x * 2)) + dist_x);
		int y = (int) (rand.nextInt((int) (this.world.getSizeY() - dist_y * 2)) + dist_y);
		Color color = bdp.getRandomBlockColor(rand);
		BlockDefault playerBlock = new BlockDefault(x, y, width, height, color, bdp.getRandomBlockTextureMap(rand));
		world.addBlock(playerBlock);
		world.setPlayer(playerBlock);
		
		System.out.println("Generation Complete");
		
	}
	
	public World getWorld() {
		return this.world;
	}
	
}
