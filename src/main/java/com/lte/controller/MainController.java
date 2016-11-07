package com.lte.controller;

import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.lte.aiPar.AlgorithmManager;
import com.lte.db.DBconnection;
import com.lte.features.SoundManager;
import com.lte.gui.Controller0;
import com.lte.gui.Controller1;
import com.lte.gui.Controller2;
import com.lte.gui.Controller3;
import com.lte.gui.Controller4;
import com.lte.interfaces.EventIM;
import com.lte.interfaces.EventIMJSON;
import com.lte.interfaces.FileIM;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.DBscoreboard;
import com.lte.models.GameDB;
import com.lte.models.GameInfo;
import com.lte.models.SetDB;
import com.lte.models.Settings;

import javafx.scene.image.Image;

/**
 * main controller; coordinates data exchange and communication between all
 * interfaces or rather controllers; also coordinates Threads
 *  
 * @author kauppfbi
 *
 */
public class MainController {
	/*
	 * Attributes
	 */

	// interface to server
	private InterfaceManager interfaceManager;

	// GUI controller
	private Controller0 controller0;
	private Controller1 controller1;
	private Controller2 controller2;
	private Controller3 controller3;
	private Controller4 controller4;

	// model(s)
	private Settings settings;
	private GameInfo gameInfo;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	AlgorithmManager algorithmManager;


	//Player KI //PlayerPlayer
	private ThreadPlayerKi threadPlayerKi;
	private ThreadPlayerPlayer threadPlayerPlayer;
	private HashMap<String, Image> images;
	private SoundManager soundManager;


	/*
	 * Constructor
	 */
	public MainController(DBconnection connection) {
		this.connection = connection;
		this.soundManager = new SoundManager();
		initImages();
	}

	private void initImages() {
		images = new HashMap<String, Image>();
		File fileButton; 
		Image imageButton; 
		
		fileButton = new File("files/images/speaker.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("speaker", imageButton);
		
		fileButton = new File("files/images/speaker-mute.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("speaker-mute", imageButton);

		fileButton = new File("files/images/speaker1.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("speaker1", imageButton);
		
		fileButton = new File("files/images/speaker1-mute.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("speaker1-mute", imageButton);
		
		fileButton = new File("files/images/play.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("play", imageButton);
		
		fileButton = new File("files/images/pause.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("pause", imageButton);
		
		fileButton = new File("files/images/stop.png");
		imageButton = new Image(fileButton.toURI().toString());
		images.put("stop", imageButton);
		
		
	}

	/*
	 * Getter and Setter
	 */
	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public void setInterfaceManager(InterfaceManager interfaceManager) {
		this.interfaceManager = interfaceManager;
	}

	public Controller0 getController0() {
		if (controller0 == null){
			this.controller0 = new Controller0(this);
			return controller0;
		}else {
		return controller0;
		}
	}

	public Controller1 getController1() {
		if (controller1 == null){
			this.controller1 = new Controller1(this);
			return controller1;
		}else {
		return controller1;
		}
	}

	public Controller2 getController2() {
		if (controller2 == null){
			this.controller2 = new Controller2(this);
			return controller2;
		}else {
		return controller2;
		}
	}
	
	public Controller3 getController3() {
		if (controller3 == null){
			this.controller3 = new Controller3(this);
			return controller3;
		}else {
		return controller3;
		}
	}
	
	public Controller4 getController4() {
		if (controller4 == null){
			this.controller4 = new Controller4(this);
			return controller4;
		}else {
		return controller4;
		}
	}

	public DBconnection getConnection() {
		return connection;
	}


	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}
	
	public DBscoreboard[] getScoreBoardInfo(){
		return connection.getScoreboard();
	}
	
	public void setThreadPlayerKiNull() {
		threadPlayerKi = null;
	}
	
	public void setThreadPlayerPlayerNull() {
		threadPlayerPlayer = null;
	}

	/*
	 * public methods <-- called by GUI-Controllers
	 */
	
	/***********************************
	 ********* Playing *****************
	 ***********************************/
	public void playSet(){
		boolean successfull = initializeInterface();
		if (!successfull) {
			JOptionPane.showInternalMessageDialog(null, "Interface wurde nicht erfolgreich initilisiert!");
		} else{
			ThreadPlay playingThread = new ThreadPlay(interfaceManager, controller1, gameInfo, connection, algorithmManager, settings);
			playingThread.start();
		}
	}
	
	/***********************************
	 ********* Playing Player KI 
	 * @return *****************
	 ***********************************/
	public int playTurnKi(int column){
		if(threadPlayerKi == null){
			threadPlayerKi = new ThreadPlayerKi(controller3, gameInfo, algorithmManager, settings, connection);
		}
			return threadPlayerKi.playTurn(column);
	}
	
	/***********************************
	 ********* Playing Player Player 
	 * @return *****************
	 ***********************************/
	public int playTurnPlayerPlayer(int column){
		if(threadPlayerPlayer == null){
			threadPlayerPlayer = new ThreadPlayerPlayer(controller4, gameInfo);
		}
			return threadPlayerPlayer.playTurn(column);
	}
	
	
	/***********************************
	 ********* Reconstruction***********
	 ***********************************/
	/* calls function "getSetInfos(GameID)" in DBconnection, creates an SetDB Array
	 * 1 SetDB Object = Infos about 1 played Set
	 * length of SetDB-Array = Number of played Sets in Game
	*/
	public SetDB[] getRecSetInfo(int GameID) {
		return connection.getSetInfos(GameID);	
	}
	
	// get gameinfo for choice in reconstruction:
	public GameDB[] getRecGameInfo() {
		return connection.getGames();
	}
	
	
	/*
	 * private methods - helping methods
	 */

	/**
	 * checks completeness of settings-object; if it is complete, the interface
	 * InterfaceManager is automatically instanciated
	 * 
	 * @return true if interface was initilaized successfully
	 */
	private boolean initializeInterface() {
		System.out.println(settings);
		if (settings.isCompleted()) {
			if (settings.getInterfaceType() == InterfaceManager.FILE_Type) {
				interfaceManager = new FileIM(settings.getContactPath(), settings.getServerChar());
			} else if (settings.getInterfaceType() == InterfaceManager.EVENT_TYPE) {
				interfaceManager = new EventIM(settings.getCredentials());
			} else if (settings.getInterfaceType() == InterfaceManager.EVENT_TYPE_JSON) {
				interfaceManager = new EventIMJSON(settings.getCredentials());
			} else {
				System.err.println("Interface konnte nicht korrekt zugeordnet und initialisiert werden!");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public HashMap<String, Image> getImages() {
		return images;
	}

	public SoundManager getSoundManager() {
		return soundManager;
	}
}
