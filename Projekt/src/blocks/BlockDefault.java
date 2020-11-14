package blocks;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import blockminigame.Game;
import blockminigame.gamelogic.World;
import blockminigame.render.TextureIO;
import blockminigame.util.SoundPlayer;

public class BlockDefault extends Block {
	
	private TextureIO texture;
	private Color color;
	private float fromX;
	private float toX;
	private float fromY;
	private float toY;
	private boolean isWall;
	private boolean isRandomMoving;
	private boolean isExploding;
	private boolean isColorChanger;
	
	public BlockDefault(float posX, float posY, float width, float height, Color color, TextureIO texture) {
		
		super(posX, posY, width, height);
		
		this.texture = texture;
		this.color = color;
		
		if (texture != null) this.calculateTextureCorners();
		
	}
	
	public BlockDefault(BlockDefault block) {
		
		super(block);
		
		this.texture = block.getTexture();
		this.color = block.getColor();
		
		if (texture != null) this.calculateTextureCorners();
		
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
	
	public void setTexture(TextureIO texture) {
		this.texture = texture;
	}
	
	public void setWall(boolean fix) {
		this.isWall = fix;
		
		if (fix) {
			this.isRandomMoving = false;
			this.isColorChanger = false;
			this.isExploding = false;
		}
	}

	public void setExploding(boolean exploding) {
		this.isExploding = exploding;
		if (exploding) {
			this.isWall = false;
			this.isRandomMoving = false;
		}
	}
	
	public void setRandomMoving(boolean moving) {
		this.isRandomMoving = moving;
		if (moving) {
			this.isWall = false;
			this.isExploding = false;
		}
	}
	
	public void setColorChanger(boolean colorChanger) {
		this.isColorChanger = colorChanger;
		if (colorChanger) {
			this.isWall = false;
		}
	}
	
	public void update(float speed, Random rand, Vector2f gravity) {
		
		if (rand.nextInt(9) == 0 && isRandomMoving) {
			
			this.motionX += rand.nextFloat() - 0.5F;
			this.motionY += rand.nextFloat() - 0.5F;
			
		}
		
		if (!isWall && isCollideable()) {
			this.posX += Math.max(Math.min(this.motionX * speed, 20), -20);
			this.posY += Math.max(Math.min(this.motionY * speed, 20), -20);
			this.motionX += gravity.x;
			this.motionY += gravity.y;
		}
		this.motionX *= Game.GRAVITY;
		this.motionY *= Game.GRAVITY;
		
		if (this.scale > 0 && !isWall) this.scale -= 0.01;
		
	}
	
	public boolean onCollide(World world, Block b1) {
		
		if (b1 instanceof BlockDefault) {
			
			BlockDefault b = (BlockDefault) b1;
			boolean isColorChangeer = b.isColorChanger() || this.isColorChanger();
			boolean isWall = b.isWall() || this.isWall();
			
			if (b.isExploding() || this.isExploding() && !isWall) {
				
				BlockDefault exploder = b.isExploding() ? b : this;
				
				float width = exploder.getWidth() / 2;
				float height = exploder.getHeight() / 2;
				float posX = exploder.getPosX();
				float posY = exploder.getPosY();
				
				exploder.setSize(width, height);
				exploder.setMotion(-1, -1);
				if (width < 8 || height < 8) {
					exploder.setExploding(false);
					exploder.setScale(10);
				}
				BlockDefault newBlock1 = new BlockDefault(exploder);
				if (width < 8 || height < 8) {
					newBlock1.setExploding(false);
					newBlock1.setScale(10);
				}
				newBlock1.setMotion(1, -1);
				newBlock1.setPosition(posX + width * 2, posY);
				world.addBlock(newBlock1);
				BlockDefault newBlock2 = new BlockDefault(exploder);
				if (width < 8 || height < 8) {
					newBlock2.setExploding(false);
					newBlock2.setScale(10);
				}
				newBlock2.setMotion(1, 1);
				newBlock2.setPosition(posX + width * 2, posY + height * 2);
				world.addBlock(newBlock2);
				BlockDefault newBlock3 = new BlockDefault(exploder);
				if (width < 8 || height < 8) {
					newBlock3.setExploding(false);
					newBlock3.setScale(10);
				}
				newBlock3.setMotion(-1, 1);
				newBlock3.setPosition(posX, posY + height * 2);
				world.addBlock(newBlock3);
				
				String sound = "sounds/explode_" + new Random().nextInt(4) + ".wav";
				SoundPlayer.playSound(sound);
				
			}
			
			if (!b.getColor().equals(this.getColor()) && !b.isWall() && !this.isWall() && !isColorChangeer) {
				
				if (Game.getInstance().getGameOverlay().invulnereableTimer <= 0) {
					this.destroyBlocks(b);
				}
				
				return false;
				
			} else {
				
				if (b.isColorChanger() && !b.getColor().equals(this.getColor()) && !b.isWall() && !this.isWall()) {
					
					if (!this.isWall()) this.setColor(b.getColor());
					
					String sound = "sounds/block_change.wav";
					SoundPlayer.playSound(sound);
					
				} else if (this.isColorChanger() && !b.getColor().equals(this.getColor()) && !b.isWall() && !this.isWall()) {
					
					if (!b.isWall()) b.setColor(this.getColor());
					
					String sound = "sounds/block_change.wav";
					SoundPlayer.playSound(sound);
					
				}
				
				return true;
				
			}
			
		}
		
		return true;
		
	}
	
	public static final Color WALL_COLOR = new Color(50, 50, 50);
	
	public void render() {
		
		long time = Game.getInstance().getTime();
		float rt = Math.max((float) (time % 1000) / 1000F, 0.2F);
		
		boolean ip = Game.getInstance().getWorld().getPlayer() == this;
		
		if (this.isWall) this.color = WALL_COLOR;
		
		GL11.glPushMatrix();
		
		if (this.texture == null) {
			
			GL11.glColor4f(this.color.getRed() / 255F, this.color.getGreen() / 255F, this.color.getBlue() / 255F, (this.color.getAlpha() / 255F) * scale != -1 ? scale : ip ? rt : 1);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(posX + width, posY);
			GL11.glVertex2f(posX, posY);
			GL11.glVertex2f(posX, posY + height);
			GL11.glVertex2f(posX + width, posY + height);
			GL11.glEnd();
			
		} else {
			
			GL11.glColor4f(this.color.getRed() / 255F, this.color.getGreen() / 255F, this.color.getBlue() / 255F, (255F / 255F) * scale != -1 ? scale : ip ? rt : 1);
			this.texture.bind();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(toX, fromY);
			GL11.glVertex2f(posX + width, posY);
			GL11.glTexCoord2f(fromX, fromY);
			GL11.glVertex2f(posX, posY);
			GL11.glTexCoord2f(fromX, toY);
			GL11.glVertex2f(posX, posY + height);
			GL11.glTexCoord2f(toX, toY);
			GL11.glVertex2f(posX + width, posY + height);
			GL11.glEnd();
			this.texture.unbind();
			
		}
		
		GL11.glPopMatrix();
		
		if (isColorChanger) {
			
			GL11.glPushMatrix();
			GL11.glLineWidth(3F);
			GL11.glColor4f(1F, 1F, 1F, (this.color.getAlpha() / 255F) * scale != -1 ? scale : ip ? rt : 1);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex2f(posX + width, posY);
			GL11.glVertex2f(posX, posY);
			GL11.glVertex2f(posX, posY + height);
			GL11.glVertex2f(posX + width, posY + height);
			GL11.glVertex2f(posX + width, posY);
			GL11.glEnd();
			GL11.glPopMatrix();
			
		}
		
		if (isExploding) {
			
			GL11.glPushMatrix();
			GL11.glLineWidth(3F);
			GL11.glColor4f(0F, 0F, 0F, (this.color.getAlpha() / 255F) * scale != -1 ? scale : ip ? rt : 1);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2f(posX + width / 2, posY);
			GL11.glVertex2f(posX + width / 2, posY + height);
			GL11.glVertex2f(posX, posY + height / 2);
			GL11.glVertex2f(posX + width, posY + height / 2);
			GL11.glEnd();
			GL11.glPopMatrix();
			
		}
		
	}
	
	public Color getColor() {
		return color;
	}
		
	public boolean isWall() {
		return this.isWall;
	}
	
	public boolean isColorChanger() {
		return isColorChanger;
	}
	
	public boolean isRandomMoving() {
		return this.isRandomMoving;
	}
	
	public boolean isExploding() {
		return isExploding;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public TextureIO getTexture() {
		return this.texture;
	}
	
}
