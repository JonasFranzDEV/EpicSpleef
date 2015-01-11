package de.oppermann.bastian.spleef.util.algorithm;

import java.util.ArrayList;
import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.World;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.arena.SpleefBlock;

public class FloodfillAlgorithm {
	
	private FloodfillAlgorithm() {
		
	}
	
	public static ArrayList<SpleefBlock> fill4(World world, int x, int y, int z, SpleefArena arena) {
		ArrayList<SpleefBlock> blocks = new ArrayList<>();
		
		Material typeFrom = world.getBlockAt(x, y, z).getType();		
		Material typeTo = typeFrom == Material.SNOW_BLOCK ? Material.WOOL : Material.SNOW_BLOCK;	// otherwise there would be an infinitive loop (if from == to)
		
		Stack<SimpleBlock> stack = new Stack<>();

		stack.push(new SimpleBlock(x, z));
		
		int counter = 0;
		
		while (!stack.empty()) {
			SimpleBlock block = stack.pop();
		 
			if (world.getBlockAt(block.getX(), y, block.getZ()).getType() == typeFrom) {
				world.getBlockAt(block.getX(), y, block.getZ()).setType(typeTo);	// sets the block to snow
				if (!arena.isArenaBlock((world.getBlockAt(block.getX(), y, block.getZ())))) {
					SpleefBlock spleefBlock = new SpleefBlock(block.getX(), y, block.getZ());
					arena.addSpleefBlock(spleefBlock);
					blocks.add(spleefBlock);
					counter++;
				}
				
				stack.push(new SimpleBlock(block.getX(), block.getZ() + 1));
				stack.push(new SimpleBlock(block.getX(), block.getZ() - 1));
				stack.push(new SimpleBlock(block.getX() + 1, block.getZ()));
				stack.push(new SimpleBlock(block.getX() - 1, block.getZ()));
			}
		}
		return blocks;
	}
	
	private static class SimpleBlock {
		
		private int x;
		private int z;
		
		public SimpleBlock(int x, int z) {
			this.x = x;
			this.z = z;
		}	
		
		public int getX() {
			return x;
		}
		
		public int getZ() {
			return z;
		}
		
	}
	
}
