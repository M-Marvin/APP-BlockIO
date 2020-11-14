package blockminigame.render;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import blockminigame.Game;

public class GameOverlay {

	public int level;
	public int elapsedTime;
	public int score;
	public int levelVariant;
	public int invulnereableTimer;
	public List<EnumGameInfoMode> gameInfoMode = new ArrayList<>();
	public static TextureIO asci_map;
	
	public GameOverlay() {
		
		File map_file = new File(Game.getInstance().getResourceFolder(), "/textures/asci_code.png");
		try {
			TextureIO map_texture = new TextureIO(new FileInputStream(map_file));
			asci_map = map_texture;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void render() {
		
		StringRenderer.drawString("Level: " + level, 25, 25, 20, new Color(255, 255, 255), asci_map);
		if (levelVariant > 0) StringRenderer.drawString("Variant: " + levelVariant, 25, 50, 20, new Color(255, 255, 255), asci_map);
		StringRenderer.drawString("Score: " + score, 300, 25, 20, new Color(255, 255, 255), asci_map);
		StringRenderer.drawString("Elapsed Time: " + new Time(elapsedTime).getMinutes() + ":" + new Time(elapsedTime).getSeconds(), 600, 25, 20, new Color(255, 255, 255), asci_map);
		if (this.invulnereableTimer > 0) StringRenderer.drawString("Invulnereable Time: " + this.invulnereableTimer, 300, 50, 20, new Color(255, 255, 255), asci_map);
		
		if (gameInfoMode != null) {
			
			boolean keyInfo = true;
			int pos = 150;
			
			for (EnumGameInfoMode infoM : this.gameInfoMode) {
				
				String[] info = infoM.getInfo();
				
				for (String s : info) {
					
					StringRenderer.drawString(s, keyInfo ? 100 : 325, pos, 20, new Color(255, 255, 255), asci_map);
					keyInfo = !keyInfo;
					if (keyInfo) pos += 25;
					
				}
				
				pos += 25;
				
			}
			
		}
		
	}
	
	public static enum EnumGameInfoMode {
		
		START_INFO(new String[] {
				"Triff den Weissen Wuerfel um zum", "",
				"naegsten Level zu kommen", "",
				"Die kollision mit andersfarbigen", "",
				"wuerfeln beended das Spiel", "",
				"", "",
				"[LSHIFT]", "> Spiel Starten"
		}),
		
		LOS_INFO(new String[] {
				"Game Over", "",
				"", "",
				"[ESC]", "> Spiel beenden"
		}),
		
		NO_INFOS(new String[] {});
		
		private String[] info;
		
		EnumGameInfoMode(String[] info) {
			this.info = info;
		}
		
		public String[] getInfo() {
			return this.info;
		}
		
	}
	
	public void addInfo(EnumGameInfoMode info) {
		if (!this.gameInfoMode.contains(info)) this.gameInfoMode.add(info);
	}
	
	public void removeInfo(EnumGameInfoMode info) {
		if (this.gameInfoMode.contains(info)) this.gameInfoMode.remove(info);
	}
	
	public void removeAllInfos() {
		this.gameInfoMode.clear();
	}
	
}
