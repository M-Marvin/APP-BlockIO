package blocks;

import java.awt.Color;
import java.util.Random;

import blockminigame.gamelogic.BlockProvoiderRegistry.BlockProvoider;
import blockminigame.gamelogic.World;
import blockminigame.render.TextureIO;
import blockminigame.util.TextureLoader;

public class BlockDefaultProvoider extends BlockProvoider<BlockDefault> {

	public static final Color[] BLOCK_COLORS = new Color[] {new Color(255, 0, 0), new Color(0, 255, 0), new Color(255, 0, 255), new Color(255, 255, 0)};
	public TextureIO[] blockTextureMaps;
	
	public BlockDefaultProvoider() {
		blockTextureMaps = new TextureIO[] {	TextureLoader.loadTexture("blocks/default_block_texture_map_2.png"), 
												TextureLoader.loadTexture("blocks/default_block_texture_map_1.png")}; 
	}
	
	@Override
	public BlockDefault provoideBlock(World world, Random rand) {
		
		boolean isBorder = rand.nextBoolean();
		boolean isRandM = rand.nextInt(5) == 0 && !isBorder;
		boolean isColorC = rand.nextInt(7) == 0 && !isBorder;
		boolean isExploding = rand.nextInt(3) == 0 && !isBorder;
		
		int width = rand.nextInt(20) + 20;
		int height = rand.nextInt(20) + 20;
		
		if (isBorder) {
			
			width = rand.nextInt(280) + 20;
			height = rand.nextInt(width > 40 ? 20 : 280) + 20;
			
		}
		
		Color color = getRandomBlockColor(rand);
		
		BlockDefault block = new BlockDefault(0, 0, width, height, color, getRandomBlockTextureMap(rand));
		block.setWall(isBorder);
		block.setRandomMoving(isRandM);
		block.setColorChanger(isColorC);
		block.setExploding(isExploding);
		
		return block;
		
	}
	
	public TextureIO getRandomBlockTextureMap(Random rand) {
		return blockTextureMaps[rand.nextInt(this.blockTextureMaps.length - 1)];
	}
	
	public Color getRandomBlockColor(Random rand) {
		return BLOCK_COLORS[rand.nextInt(BLOCK_COLORS.length - 1)];
	}
	
	@Override
	public int getChance() {
		return 0;
	}

}
