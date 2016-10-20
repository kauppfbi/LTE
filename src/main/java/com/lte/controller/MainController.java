package com.lte.controller;

import javax.swing.JOptionPane;

import com.lte.aiPar.AlgorithmusManager;
import com.lte.db.DBconnection;
import com.lte.gui.Controller0;
import com.lte.gui.Controller1;
import com.lte.gui.Controller2;
import com.lte.interfaces.EventIM;
import com.lte.interfaces.EventIMJSON;
import com.lte.interfaces.FileIM;
import com.lte.interfaces.InterfaceManager;
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
	AlgorithmusManager algorithmManager;

	/*
	 * Constructor
	 */
	public MainController(DBconnection connection) {
		this.connection = connection;
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
		return controller0;
	}

	public void setController0(Controller0 controller0) {
		this.controller0 = controller0;
	}

	public Controller1 getController1() {
		return controller1;
	}

	public void setController1(Controller1 controller1) {
		this.controller1 = controller1;
	}

	public Controller2 getController2() {
		return controller2;
	}

	public void setController2(Controller2 controller2) {
		this.controller2 = controller2;
	}

	public DBconnection getConnection() {
		return connection;
	}

	public void setConnection(DBconnection connection) {
		this.connection = connection;
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
			ThreadPlay playingThread = new ThreadPlay(interfaceManager, controller1, gameInfo, connection, algorithmManager);
			playingThread.start();
		}
	}
	
	
	/***********************************
	 ********* Reconstruction***********
	 ***********************************/
	
//  Das ist veraltet!!!
//	public int[] getReplayTurns(int GameID, int setNumber) {
//		return connection.getReplayTurns(GameID, setNumber);
//	}
	
//  Nutze Stattdessen getSetInfos, welches in den int[] replayTurns fuellt!!
	public SetDB[] getSetInfos(int GameID) {
		return connection.getSetInfos(GameID);	
	}
	
	// get gameinfo for coice in reconstruction:
	public GameDB[] getRecGameInfo() {
		return connection.getGames();
	}

	// get setnumbers for choice in reconstruction:
//	public GameDB[] getRecSetNumber(int gameID) {
//		return connection.getSetInfos(gameID);
//	}

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
				interfaceManager = new EventIM();
			} else if (settings.getInterfaceType() == InterfaceManager.EVENT_TYPE_JSON) {
				interfaceManager = new EventIMJSON();
			} else {
				System.err.println("Interface konnte nicht korrekt zugeordnet und initialisiert werden!");
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
}
