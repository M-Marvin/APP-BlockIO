package blocks;

import java.util.Random;

import blockminigame.gamelogic.BlockProvoiderRegistry.BlockProvoider;
import blockminigame.gamelogic.World;

public class BlockGoalProvoider extends BlockProvoider<BlockGoal> {
	
	@Override
	public BlockGoal provoideBlock(World world, Random rand) {
		
		int width = rand.nextInt(20) + 20;
		int height = rand.nextInt(20) + 20;
		
		BlockGoal block = new BlockGoal(0, 0, width, height);
		
		return block;
		
	}
	
	@Override
	public int getChance() {
		return 101; // Dont genarate
	}

}