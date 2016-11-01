package com.lte.features;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
	
	public void pause(){
		mediaPlayer.pause();
	}
}