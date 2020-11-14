package blocks;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import blockminigame.Game;
import blockminigame.gamelogic.World;
import blockminigame.render.TextureIO;
import blockminigame.util.SoundPlayer;

public abstract class Block {
	
	public float posX;
	public float posY;
	public float width;
	public float height;
	public float motionX;
	public float motionY;
	public float scale;
	
	public Block(float posX, float posY, float width, float height) {
		
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.scale = -1;
		
	}
	
	public Block(Block block) {
		
		this.scale = -1;
		this.width = block.getWidth();
		this.height = block.getHeight();
		this.motionX = block.getMotionX();
		this.motionY = block.getMotionY();
		
	}
	
	public float[] getTextureCorners() {
		
		return new float[] {0, 1, 0, 1};
		
	}
	
	public void setPosition(float x, float y) {
		this.posX = x;
		this.posY = y;
	}
	
	public boolean isCordInBlock(float x, float y) {
		
		return posX < x && posX + width > x && posY < y && posY + height > y;
		
	}
	
	public void setMotion(float x, float y) {
		this.motionX = x;
		this.motionY = y;
	}
	
	public void update(float speed, Random rand, Vector2f gravity) {
		
		if (isCollideable()) {
			this.posX += Math.max(Math.min(this.motionX * speed, 20), -20);
			this.posY += Math.max(Math.min(this.motionY * speed, 20), -20);
			this.motionX += gravity.x;
			this.motionY += gravity.y;
		}
		this.motionX *= Game.GRAVITY;
		this.motionY *= Game.GRAVITY;
		
		if (this.scale > 0) this.scale -= 0.01;
		
	}
	
	public void destroyBlocks(Block secondBlock) {
		
		secondBlock.setScale(1);
		secondBlock.setMotion(0, 0);
		
		this.destroyBlock();
		
	}
	
	public void destroyBlock() {
		
		this.setScale(1);
		this.setMotion(0, 0);
		
		String sound = "sounds/block_destroy.wav";
		SoundPlayer.playSound(sound);
		
	}
	
	public abstract boolean onCollide(World world, Block b);
	
	public boolean isBlockExisting() {
		return (scale > 0 || scale == -1);
	}
	
	public void render() {
		
		GL11.glPushMatrix();

		float[] corners = getTextureCorners();
		
		this.getTexture().bind();
		GL11.glColor4f(1F, 1F, 1F, 1F * (scale != -1 ? scale : 1));
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(corners[1], corners[2]);
		GL11.glVertex2f(posX + width, posY);
		GL11.glTexCoord2f(corners[0], corners[2]);
		GL11.glVertex2f(posX, posY);
		GL11.glTexCoord2f(corners[0], corners[3]);
		GL11.glVertex2f(posX, posY + height);
		GL11.glTexCoord2f(corners[1], corners[3]);
		GL11.glVertex2f(posX + width, posY + height);
		GL11.glEnd();
		this.getTexture().unbind();
		
		GL11.glPopMatrix();
		
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getPosX() {
		return posX;
	}
	
	public void setSize(float x, float y) {
		this.width = x;
		this.height = y;
	}
	
	public float getPosY() {
		return posY;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getMotionX() {
		return motionX;
	}
	
	public boolean isCollideable() {
		return scale == -1 || scale > 1;
	}
	
	public float getMotionY() {
		return motionY;
	}
	
	public float getScale() {
		return scale;
	}
	
	public abstract TextureIO getTexture();
	
}