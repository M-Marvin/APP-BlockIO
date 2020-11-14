package blocks;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import blockminigame.Game;
import blockminigame.gamelogic.World;
import blockminigame.render.TextureIO;
import blockminigame.util.TextureLoader;

public class BlockGoal extends Block {

	public float toX;
	public float fromX;
	public float toY;
	public float fromY;
	
	public BlockGoal(BlockGoal block) {
		super(block);
		this.fromX = block.fromX;
		this.fromY = block.fromY;
		this.toX = block.toX;
		this.toY = block.toY;
	}
	
	public BlockGoal(float posX, float posY, float width, float height) {
		super(posX, posY, width, height);
		calculateTextureCorners();
	}

	private void calculateTextureCorners() {

		Random rand = new Random();
		
		float x = rand.nextFloat();
		float y = rand.nextFloat();
		
		this.toX = this.width / 100 + x;
		this.fromX = x;
		this.toY = this.height / 100 + y;
		this.fromY = y;
		
	}
	
	@Override
	public boolean onCollide(World world, Block b) {
		
		if (world.getPlayer() == b) {
			
			this.destroyBlocks(b);
			return false;
			
		} else {
			
			return true;
			
		}
		
	}
	
	@Override
	public void update(float speed, Random rand, Vector2f gravity) {
		super.update(speed, rand, gravity);
		if (!this.isBlockExisting()) Game.getInstance().winGame();
	}
	
	@Override
	public float[] getTextureCorners() {
		
		return new float[] {fromX, toX, fromY, toY};
		
	}

	@Override
	public TextureIO getTexture() {
		
		return TextureLoader.loadTexture("blocks/goal.png");
		
	}
	
}
