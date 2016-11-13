package com.lte.features;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * This class manages the sound output within the game.
 * @author kauppfbi
 *
 */
public class SoundManager {
	private String path;
	private Media media;
	private MediaPlayer mediaPlayer;
	
	/**
	 * Default constructor, which loads the matrix soundtrack.
	 */
	public SoundManager(){
		this.path = "files/music/matrix.mp3";
		this.media = new Media(new File(path).toURI().toString());
		this.mediaPlayer = new MediaPlayer(media);
	}
	
	/**
	 * Call this method for initial playing the soundtrack
	 */
	public void play(){
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				mediaPlayer.play();
			}	
		});
	}
	
	/**
	 * Call this method to switch from PLAYING to PAUSED and from PAUSED to PLAYING
	 * @return the current Status after switching the Status
	 */
	public MediaPlayer.Status playPause(){
		if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
			mediaPlayer.pause();
			return MediaPlayer.Status.PAUSED;
		} else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED){
			mediaPlayer.play();
			return MediaPlayer.Status.PLAYING;
		} else {
			System.err.println("Unknown MediaPlayer-Status!");
			return null;
		}
	}

	/**
	 * Default Getter for mediaPlayer-Status.
	 * @return the current status of the mediaplayer.
	 */
	public Status getStatus() {
		return mediaPlayer.getStatus();
	}
}