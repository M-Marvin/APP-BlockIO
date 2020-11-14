package blockminigame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import blockminigame.Game;
import blockminigame.render.TextureIO;

public class TextureLoader {
	
	protected static HashMap<String, TextureIO> textureBuffer = new HashMap<String, TextureIO>();
	
	public static TextureIO loadTexture(String texture) {
		
		TextureIO tex = textureBuffer.get(texture);
		
		if (tex == null) {
			
			try {

				File texPath = new File(Game.getInstance().getResourceFolder(), "/textures/" + texture);
				FileInputStream is = new FileInputStream(texPath);
				tex = new TextureIO(is);
				is.close();
				textureBuffer.put(texture, tex);
				
			} catch (FileNotFoundException e) {
				System.err.println("ERROR, Cant load Texture " + texture);
			} catch (IOException e) {
				System.err.println("ERROR, Exception on loading Texture " + texture);
				e.printStackTrace();
			}
			
		}

		return tex;
		
	}
	
}
