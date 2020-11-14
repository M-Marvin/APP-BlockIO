package blockminigame.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import blockminigame.Game;

public class SoundPlayer {
	
	private HashMap<String, Clip> clipList;
	private static  int playTimer;
	
	public SoundPlayer() {
		clipList = new HashMap<>();
	}
	
	public static void playSound(String res) {

		if (playTimer == 0) {
			
			playTimer = 5;
			
			File file = new File(Game.getInstance().getResourceFolder(), res);
			
	        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {

	            Clip clip = AudioSystem.getClip();
	            clip.open(ais);
	            clip.start();
	            
	        } catch (LineUnavailableException e1) {
				System.out.println("1 Can not play " + file.getAbsolutePath() + "!");
			} catch (IOException e1) {
				System.out.println("2 Can not play " + file.getAbsolutePath() + "!");
			} catch (UnsupportedAudioFileException e2) {
				System.out.println("3 Can not play " + file.getAbsolutePath() + "!");
			}
			
		}
		
	}
	
	public void playSoundInBuffer(String soundName, String res) {
			
		if (playTimer == 0) {

			playTimer = 5;
			
			File file = new File(Game.getInstance().getResourceFolder(), res);
			
	        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {

	            Clip clip = AudioSystem.getClip();
	            clip.open(ais);
	            clipList.put(soundName, clip);
	            clip.start();
	            
	        } catch (LineUnavailableException e1) {
				System.out.println("Can not play " + file.getAbsolutePath() + "!");
			} catch (IOException e1) {
				System.out.println("Can not play " + file.getAbsolutePath() + "!");
			} catch (UnsupportedAudioFileException e2) {
				System.out.println("Can not play " + file.getAbsolutePath() + "!");
			}
			
		}
		
	}
	
	public void createLoop(String res, String loopName, int loops) {
		
		File file = new File(Game.getInstance().getResourceFolder(), res);
		
		if (!clipList.containsKey(loopName)) {
			
	        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
	        	
	            Clip clip = AudioSystem.getClip();
	            clip.open(ais);
	            clipList.put(loopName, clip);
	            clip.loop(loops);
	            
	        } catch (LineUnavailableException e1) {
				System.out.println("Can not play " + file.getAbsolutePath() + "!");
			} catch (IOException e1) {
				System.out.println("Can not play " + file.getAbsolutePath() + "!");
			} catch (UnsupportedAudioFileException e2) {
				System.out.println("Can not play " + file.getAbsolutePath() + "!");
			}
			
		}
		
	}
	
	public static void update() {
		
		if (playTimer > 0) playTimer--;
		
	}
	
	public void cancelLoop(String loopName) {
		if (clipList.containsKey(loopName)) {
			clipList.get(loopName).stop();
			clipList.remove(loopName);
		}
	}
	
	public boolean isLoopRunning(String loopName) {
		
		if (clipList.containsKey(loopName)) {
			
			if (clipList.get(loopName).isRunning()) {
				return true;
			} else {
				clipList.remove(loopName);
				return false;
			}
			
		}
		
		return false;
		
	}
	
	public void onClearBuffer() {
		for (Clip clip : clipList.values()) {
			clip.stop();
		}
		clipList.clear();
	}
	
}
