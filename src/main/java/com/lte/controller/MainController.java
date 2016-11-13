package com.lte.controller;

import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;
import com.lte.aiPar.AlgorithmManager;
import com.lte.features.SoundManager;
import com.lte.gui.ControllerStart;
import com.lte.gui.ControllerKiKi;
import com.lte.gui.ControllerReconstruct;
import com.lte.gui.ControllerPlayerKi;
import com.lte.gui.ControllerPlayerPlayer;
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
	private ControllerStart controllerStart;
	private ControllerKiKi controllerKiKi;
	private ControllerReconstruct controllerReconstruct;
	private ControllerPlayerKi controllerPlayerKi;
	private ControllerPlayerPlayer controllerPlayerPlayer;

	// model(s)
	private Settings settings;
	private GameInfo gameInfo;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	private AlgorithmManager algorithmManager;
	
	//threads
	private ThreadPlay threadPlay;
	private ThreadPlayerKi threadPlayerKi;
	private ThreadPlayerPlayer threadPlayerPlayer;
	private HashMap<String, Image> images;
	private SoundManager soundManager;


	/**
	 * default constructor<br>
	 * needs a DBconnection object as parameter
	 * @param connection
	 */
	public MainController(DBconnection connection) {
		this.algorithmManager = new AlgorithmManager();
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
	
	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene0/layout0, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller0-Object (GUI-Controller for scene0)
	 */
	public ControllerStart getControllerStart() {
		if (controllerStart == null){
			this.controllerStart = new ControllerStart(this);
			return controllerStart;
		}else {
		return controllerStart;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene1/layout1, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller1-Object (GUI-Controller for scene1)
	 */
	public ControllerKiKi getControllerKiKi() {
		if (controllerKiKi == null){
			this.controllerKiKi = new ControllerKiKi(this);
			return controllerKiKi;
		}else {
		return controllerKiKi;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene2/layout2, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller2-Object (GUI-Controller for scene2)
	 */
	public ControllerReconstruct getControllerReconstruct() {
		if (controllerReconstruct == null){
			this.controllerReconstruct = new ControllerReconstruct(this);
			return controllerReconstruct;
		}else {
		return controllerReconstruct;
		}
	}
	
	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene3/layout3, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller3-Object (GUI-Controller for scene3)
	 */
	public ControllerPlayerKi getControllerPlayerKi() {
		if (controllerPlayerKi == null) {
			this.controllerPlayerKi = new ControllerPlayerKi(this);
			return controllerPlayerKi;
		} else {
			return controllerPlayerKi;
		}
	}
	
	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene4/layout4, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller4-Object (GUI-Controller for scene4)
	 */
	public ControllerPlayerPlayer getControllerPlayerPlayer() {
		if (controllerPlayerPlayer == null){
			this.controllerPlayerPlayer = new ControllerPlayerPlayer(this);
			return controllerPlayerPlayer;
		}else {
		return controllerPlayerPlayer;
		}
	}
	
	public ThreadPlay getThreadPlay(){
		return threadPlay;
	}
	
	public ThreadPlayerKi getThreadPlayerKi() {
		if (threadPlayerKi == null || threadPlayerKi.getState() == Thread.State.TERMINATED){
			threadPlayerKi = new ThreadPlayerKi(controllerPlayerKi, gameInfo, settings, algorithmManager, connection);
			threadPlayerKi.start();
		}
		return threadPlayerKi;
	}

	public SoundManager getSoundManager() {
		return soundManager;
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
	
	public void setThreadPlayerPlayerNull() {
		threadPlayerPlayer = null;
	}

	/*
	 * public methods <-- called by GUI-Controllers
	 */
	/**
	 * This method is called by Controller1, triggered by a User-Action.<br>
	 * It initializes the interface and instantiates a new Thread which takes over the communication between the modules. 
	 */
	public void playSet(){
		boolean successfull = initializeInterface();
		if (!successfull) {
			JOptionPane.showInternalMessageDialog(null, "Interface wurde nicht erfolgreich initilisiert!");
		} else{
			resetAlgorithmManager();
			threadPlay = new ThreadPlay(interfaceManager, controllerKiKi, gameInfo, connection, algorithmManager, settings);
			threadPlay.start();
		}
	}
	
	/***********************************
	 ********* Playing Player KI 
	 * @return *****************
	 ***********************************/
//	public int playTurnKi(int column) {
//		resetAlgorithmManager();
//		if (threadPlayerKi == null) {
//			threadPlayerKi = new ThreadPlayerKi(controller3, gameInfo, algorithmManager, settings, connection);
//		}
//		return threadPlayerKi.playTurn(column);
//	}
	
	/***********************************
	 ********* Playing Player Player 
	 * @return 
	 * @throws Exception *****************
	 ***********************************/
	public int playTurnPlayerPlayer(int column) throws Exception{
		if(threadPlayerPlayer == null){
			threadPlayerPlayer = new ThreadPlayerPlayer(controllerPlayerPlayer, gameInfo);
		}
			return threadPlayerPlayer.playTurn(column);
	}
	
	/**
	 * Conducts DB Query.
	 * @param GameID
	 * @return Array of SetDB-Objects (meta-information for every Set in DB, which is connected to the game with the transfered gaemID)
	 */
	public SetDB[] getRecSetInfo(int GameID) {
		if (isConnectionFree()){
			return connection.getSetInfos(GameID);	
		} else {
			return null;
		}
	}
	
	/**
	 * Conducts DB Query.
	 * 
	 * @return Array of GameDB-Objects (meta-information for every game in DB)
	 */
	public GameDB[] getRecGameInfo() {
		if (isConnectionFree()) {
			return connection.getGames();
		} else {
			return null;
		}
	}

	/**
	 * Conducts DB Query.
	 * 
	 * @return Array of Strings, filled with opponent names
	 */
	public String[] getOpponentNames() {
		if (isConnectionFree()) {

			return connection.getOpponentNames();
		} else {
			return null;
		}
	}

	/**
	 * Method deletes uncompleted DB entries, if game is aborted by user.
	 * 
	 * @return true, if query was successfully
	 */
	public boolean deleteUnfinishedGame() {
		if (isConnectionFree()) {
			connection.deleteUnfinishedGame(gameInfo.getGameID());
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * private methods - helping methods
	 */

	/**
	 * checks completeness of settings-object; if it is complete, the interface
	 * InterfaceManager is automatically instantiated
	 * 
	 * @return true if interface was initialized successfully
	 */
	private boolean initializeInterface() {
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

	/**
	 * This method evaluates, if the dbConnection object can be used.<br>
	 * This method should be used before calling DB-methods to avoid SQL-Exceptions.
	 * @return true, if the connection-Object can be used without problems.
	 */
	private boolean isConnectionFree (){
		if (threadPlay == null){
			return true;
		} else {
			if(threadPlay.getState() == Thread.State.NEW || threadPlay.getState() == Thread.State.TERMINATED){
				return true;
			} else {
				System.err.println("DB Connection belegt!");
				return false;
			}
		}
	}
	
	private void resetAlgorithmManager(){
		algorithmManager.shutdown();
		this.algorithmManager = new AlgorithmManager();
	}
	
	public void shutdownApplication(){
		// DB: delete unfinished game
		if(gameInfo != null){
			if(!(gameInfo.getOwnPoints() == 3 || gameInfo.getOpponentPoints() == 3)) {
				deleteUnfinishedGame();
			}
		}
		if(algorithmManager != null){
			algorithmManager.shutdown();
		}
		if(controllerStart!= null){
			controllerStart.exitApplication();
		}
		if(controllerKiKi != null){
			controllerKiKi.exitApplication();
		}
		if(controllerReconstruct != null){
			controllerReconstruct.exitApplication();
		}
		if(controllerPlayerKi != null){
			controllerPlayerKi.exitApplication();
		}
		if(controllerPlayerPlayer != null){
			controllerPlayerPlayer.exitApplication();
		}
	}
}
