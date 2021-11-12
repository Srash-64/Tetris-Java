package tetris;

import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Field;

public class MusicPlayer  {

	
    //Creating FileChooser for choosing the music mp3 file
    JFileChooser fileChooser;
    InputStream fileInputStream;
    BufferedInputStream bufferedInputStream;
    File myFile = null;
    String filePath;
    long totalLength, pauseLength;
    Player player;
    Thread playThread, resumeThread;


    
    public MusicPlayer(String filePath) {
    	this.filePath = filePath;

        //Calling Threads
        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);

    }


    public void play() {        	
        //starting play thread
        if (filePath != null) {
        	 playThread = new Thread(runnablePlay);
            playThread.start();
                    }
        }
    
        public void pause() {
        //code for pause button
        if (player != null && filePath != null) {
            try {
                pauseLength = fileInputStream.available();
                player.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

        public void resume() {
        //starting resume thread
        if (filePath != null) {
            resumeThread = new Thread(runnableResume);
            resumeThread.start();
        } else {
    
        }
    }
        
        public void stop() {
        //code for stop button
        if (player != null) {
            player.close();
          

    }
        }


    Runnable runnablePlay = new Runnable() {
        @Override
        public void run() {            try {
            	
                //code for play button
                fileInputStream = Tetris.class.getResourceAsStream("/" + filePath);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                totalLength = fileInputStream.available();
                player.play();//starting music
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableResume = new Runnable() {
        @Override
        public void run() {
            try {
                //code for resume button
            	 fileInputStream = Tetris.class.getResourceAsStream("/" + filePath);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                fileInputStream.skip(totalLength - pauseLength);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}