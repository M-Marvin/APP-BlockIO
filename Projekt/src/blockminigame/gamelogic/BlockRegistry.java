package blockminigame.gamelogic;

import java.util.HashMap;

import blocks.Block;

public class BlockRegistry {
	
	protected static BlockRegistry instance;
	private HashMap<String, Class<? extends Block>> blockRegistry = new HashMap<String, Class<? extends Block>>();
	
	public static BlockRegistry getRegistry() {
		return instance;
	}
	
	public void registerBlock(String name, Class<? extends Block> blockClass) {
		this.blockRegistry.put(name, blockClass);
	}
	
	public Class<? extends Block> getBlockClass(String name) {
		return this.blockRegistry.get(name);
	}
	
	public String[] getBlocks() {
		return this.blockRegistry.keySet().toArray(new String[] {});
	}
 	
}
