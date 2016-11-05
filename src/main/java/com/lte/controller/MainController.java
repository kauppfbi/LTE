package com.lte.controller;

import javax.swing.JOptionPane;
import com.lte.aiPar.AlgorithmManager;
import com.lte.db.DBconnection;
import com.lte.gui.Controller0;
import com.lte.gui.Controller1;
import com.lte.gui.Controller2;
import com.lte.interfaces.EventIM;
import com.lte.interfaces.EventIMJSON;
import com.lte.interfaces.FileIM;
import com.lte.interfaces.InterfaceManager;
import com.lte.models.DBscoreboard;
import com.lte.models.GameDB;
import com.lte.models.GameInfo;
import com.lte.models.SetDB;
import com.lte.models.Settings;

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

	// model(s)
	private Settings settings;
	private GameInfo gameInfo;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	private AlgorithmManager algorithmManager;
	
	//threads
	private ThreadPlay playingThread;

	/**
	 * default constructor<br>
	 * needs a DBconnection object as parameter
	 * @param connection
	 */
	public MainController(DBconnection connection) {
		this.connection = connection;
	}

	/*
	 * Getter and Setter
	 */
	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene0/layout0, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller0-Object (GUI-Controller for scene0)
	 */
	public Controller0 getController0() {
		if (controller0 == null){
			this.controller0 = new Controller0(this);
			return controller0;
		}else {
		return controller0;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene1/layout1, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller1-Object (GUI-Controller for scene1)
	 */
	public Controller1 getController1() {
		if (controller1 == null){
			this.controller1 = new Controller1(this);
			return controller1;
		}else {
		return controller1;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene2/layout2, the controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call its constructor and returns the new object.
	 * 
	 * @return Controller2-Object (GUI-Controller for scene2)
	 */
	public Controller2 getController2() {
		if (controller2 == null){
			this.controller2 = new Controller2(this);
			return controller2;
		}else {
		return controller2;
		}
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
			playingThread = new ThreadPlay(interfaceManager, controller1, gameInfo, connection, algorithmManager, settings);
			playingThread.start();
		}
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
	

	/**
	 * This method evaluates, if the dbConnection object can be used.<br>
	 * This method should be used before calling DB-methods to avoid SQL-Exceptions.
	 * @return true, if the connection-Object can be used without problems.
	 */
	private boolean isConnectionFree (){
		if (playingThread == null){
			return true;
		} else {
			if(playingThread.getState() == Thread.State.NEW || playingThread.getState() == Thread.State.TERMINATED){
				return true;
			} else {
				return false;
			}
		}
	
	}
}
