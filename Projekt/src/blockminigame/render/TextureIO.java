package blockminigame.render;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TextureIO {

	public static final TextureIO DEFAULT_TEXTURE = new TextureIO();
	
    private final IntBuffer texture;
    private final int       width;
    private final int       height;
    private final boolean   isOpquape;
    
    private int             id;
    
    
    public TextureIO(final InputStream inputStream) throws IOException {

        BufferedImage image = ImageIO.read(inputStream);
        
        width = image.getWidth();
        height = image.getHeight();

        boolean opquape = true;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	if (opquape) opquape = ((image.getRGB(x, y) >> 24) & 0xFF) == 255;
            }
        }
        isOpquape = opquape;
        
        final AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -height);

        final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);

        final int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        texture = BufferUtils.createIntBuffer(pixels.length);
        texture.put(pixels);
        texture.rewind();
        
        this.init();
        
    }
    
    public int getWidth() {
		return width;
	}
    
    public int getHeight() {
		return height;
	}
    
    public boolean isTransculent() {
    	
    	return !isOpquape;
    	
    }
    
    public TextureIO() {
    	
    	isOpquape = true;
    	width = 2;
    	height = 2;
    	texture = BufferUtils.createIntBuffer(4);
    	texture.put(new Color(255, 0, 255).getRGB());
    	texture.put(new Color(0, 0, 0).getRGB());
    	texture.put(new Color(0, 0, 0).getRGB());
    	texture.put(new Color(255, 0, 255).getRGB());
    	texture.rewind();
    	
    	init();
    	
    }


    public void init() {

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        final IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(buffer);
        id = buffer.get(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, texture);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

    }


    public void bind() {

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

    }


    public void unbind() {

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

    }


}
