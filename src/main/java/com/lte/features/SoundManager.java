package com.lte.features;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class SoundManager {
	String path;
	Media media;
	MediaPlayer mediaPlayer;
	
	public SoundManager(){
		this.path = "files/music/matrix.mp3";
		this.media = new Media(new File(path).toURI().toString());
		this.mediaPlayer = new MediaPlayer(media);
	}
	
	
	public void play(){
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				mediaPlayer.play();
			}	
		});
	}
	
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

	public Status getStatus() {
		return mediaPlayer.getStatus();
	}
}