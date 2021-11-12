package tetris;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class SoundPlayer {
	
	private String soundName;
	private int pausedOnFrame = 0;
	private AdvancedPlayer player;
	
	public SoundPlayer(String soundName) {
		this.soundName = soundName;
		
		
	      
		 
		
	}
	
	public void start() {
		if(player != null) {
			player.stop();
		}
		
		   try {       				  
			   InputStream input = Tetris.class.getResourceAsStream("/" + soundName);
			   
			   try {
					player = new AdvancedPlayer(input);
					player.setPlayBackListener(new PlaybackListener(){


						//override unimplemented methods
						@Override
						public void playbackFinished(PlaybackEvent evt){
						    try{
							stop();
						    }
						    catch(Exception ex){System.out.println(ex);}
						}

						@Override
						public void playbackStarted(PlaybackEvent evt){
						}
					});
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   
	              player.play();
	          }catch(JavaLayerException e) {
	              e.printStackTrace();
	          }
	}
	
	public void stop() {
		player.stop();		
	}

}
