package blockminigame.gamelogic;

import java.util.HashMap;
import java.util.Random;

import blocks.Block;

@SuppressWarnings("rawtypes")
public class BlockProvoiderRegistry {

	protected static BlockProvoiderRegistry instance = new BlockProvoiderRegistry();
	private HashMap<Class<? extends Block>, BlockProvoider> blockProvoiderMap = new HashMap<Class<? extends Block>, BlockProvoider>();
	
	public static BlockProvoiderRegistry getRegistry() {
		return instance;
	}
	
	public void registerBlockProvoider(BlockProvoider provoider, Class<? extends Block> block) {
		this.blockProvoiderMap.put(block, provoider);
	}
	
	public BlockProvoider getProvoider(Class<? extends Block> block) {
		return blockProvoiderMap.get(block);
	}
	
	public BlockProvoider[] getBlockProvoiders() {
		return this.blockProvoiderMap.values().toArray(new BlockProvoider[] {});
	}
	
	public static abstract class BlockProvoider<T extends Block> {
		
		public abstract T provoideBlock(World world, Random rand);
		
		public abstract int getChance();
		
	}
	
}
