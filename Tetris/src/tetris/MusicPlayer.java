package tetris;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends Thread {

    private static String fichier="";
    private static int nbConfig=0;
    private static Thread enCours=null;
    private AdvancedPlayer player;
    private boolean exit;
    /**
     * Permet de créer le morceau de musique qui sera joué en boucle.<br />
     * Attention, ne fonctionne qu'avec des fichiers mp3.<br />
     * Pour lancer la musique, il faut appeler la méthode lecture.
     * @param fic Le chemin vers le fichier de la musique.
     */
    public MusicPlayer(String fic) throws java.lang.RuntimeException{
    	fichier = fic;
    	exit = false;
		InputStream is = this.getClass().getResourceAsStream(fic);
		try {
			player = new AdvancedPlayer(is);
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		enCours=this;
	
    }

 // for stopping the thread
    public void exit()
    {
        exit = true;
        if(player != null)  player.close();
    }



    /**
     * Lance la lecture de la musique de fond.
     */
    public void lecture(){
	start();
    }

    /**
     * Stoppe la musique.
     */
    public void arret(){
	player.close();
    }

    @Override
    public void run() {
	try{
		while(!exit) {
			InputStream is = this.getClass().getResourceAsStream(fichier);
			try {
				player = new AdvancedPlayer(is);
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			player.play();
		}
			
		}
	catch(Exception ex){}
	player.close();
    }
 }

