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
 * The main controller class <br>
 * coordinates data exchange and communication between all interfaces or rather
 * controllers<br>
 * and coordinates Threads
 * 
 * @author kauppfbi
 *
 */
public class MainController {

	// interface to server
	private InterfaceManager interfaceManager;

	// GUI controller
	private ControllerStart controllerStart;
	private ControllerKiKi controllerKiKi;
	private ControllerReconstruct controllerReconstruct;
	private ControllerPlayerKi controllerPlayerKi;
	private ControllerPlayerPlayer controllerPlayerPlayer;

	// models
	private Settings settings;
	private GameInfo gameInfo;

	// DB "Manager"
	private DBconnection connection;

	// KI Manager
	private AlgorithmManager algorithmManager;

	// threads
	private ThreadPlay threadPlay;
	private ThreadPlayerKi threadPlayerKi;
	private ThreadPlayerPlayer threadPlayerPlayer;

	// HashMap, which holds several images
	private HashMap<String, Image> images;

	// Manager for sounds and music in gameplay
	private SoundManager soundManager;

	/**
	 * default constructor<br>
	 * needs a DBconnection object as parameter
	 * 
	 * @param connection
	 */
	public MainController(DBconnection connection) {
		this.algorithmManager = new AlgorithmManager();
		this.connection = connection;
		this.soundManager = new SoundManager();
		initImages();
	}

	/*
	 * Getter for GUI Controllers
	 */

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene0/layout0 (start), the
	 * controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call
	 * its constructor and returns the new object.
	 * 
	 * @return ControllerStart-Object (GUI-Controller for scene0)
	 */
	public ControllerStart getControllerStart() {
		if (controllerStart == null) {
			this.controllerStart = new ControllerStart(this);
			return controllerStart;
		} else {
			return controllerStart;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene1/layout1 (ai vs. ai), the
	 * controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call
	 * its constructor and returns the new object.
	 * 
	 * @return ControllerKiKi-Object (GUI-Controller for scene1)
	 */
	public ControllerKiKi getControllerKiKi() {
		if (controllerKiKi == null) {
			this.controllerKiKi = new ControllerKiKi(this);
			return controllerKiKi;
		} else {
			return controllerKiKi;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene2/layout2 (Reconstruction), the
	 * controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call
	 * its constructor and returns the new object.
	 * 
	 * @return ControllerReconstruct-Object (GUI-Controller for scene2)
	 */
	public ControllerReconstruct getControllerReconstruct() {
		if (controllerReconstruct == null) {
			this.controllerReconstruct = new ControllerReconstruct(this);
			return controllerReconstruct;
		} else {
			return controllerReconstruct;
		}
	}

	/**
	 * This method provides a object-recycling-function.<br>
	 * In case of a scene change to scene3/layout3 (Player vs. AI), the
	 * controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call
	 * its constructor and returns the new object.
	 * 
	 * @return ControllerPlayerKi-Object (GUI-Controller for scene3)
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
	 * In case of a scene change to scene4/layout4 (Player vs. Player), the
	 * controller-object can be used again.<br>
	 * If the mainController did not instantiate a object yet, the method call
	 * its constructor and returns the new object.
	 * 
	 * @return ControllerPlayerPlayer-Object (GUI-Controller for scene4)
	 */
	public ControllerPlayerPlayer getControllerPlayerPlayer() {
		if (controllerPlayerPlayer == null) {
			this.controllerPlayerPlayer = new ControllerPlayerPlayer(this);
			return controllerPlayerPlayer;
		} else {
			return controllerPlayerPlayer;
		}
	}

	/*
	 * Getter for Threads
	 */

	public ThreadPlay getThreadPlay() {
		return threadPlay;
	}

	/**
	 * Modified Getter-Method, which always returns a running thread.
	 * 
	 * @return running thread <i>ThreadPlayerKi</i>
	 */
	public ThreadPlayerKi getThreadPlayerKi() {
		if (threadPlayerKi == null || threadPlayerKi.getState() == Thread.State.TERMINATED) {
			threadPlayerKi = new ThreadPlayerKi(controllerPlayerKi, gameInfo, settings, algorithmManager, connection);
			threadPlayerKi.start();
		}
		return threadPlayerKi;
	}

	public ThreadPlayerPlayer getThreadPlayerPlayer() {
		return threadPlayerPlayer;
	}

	/*
	 * Getter and Setter for models
	 */

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
	 * Other getters
	 */

	public SoundManager getSoundManager() {
		return soundManager;
	}

	public HashMap<String, Image> getImages() {
		return images;
	}

	/*
	 * DB-Methods
	 */

	/**
	 * Conducts DB Query.
	 * 
	 * @return Array DBscoreboard-objects
	 */
	public DBscoreboard[] getScoreBoardInfo() {
		return connection.getScoreboard();
	}

	/**
	 * Conducts DB Query.
	 * 
	 * @param GameID
	 * @return Array of SetDB-Objects (meta-information for every Set in DB,
	 *         which is connected to the game with the transfered gaemID)
	 */
	public SetDB[] getRecSetInfo(int GameID) {
		if (isConnectionFree()) {
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
	 * other public methods
	 */

	/**
	 * This method is called by Controller1, triggered by a User-Action.<br>
	 * It initializes the interface and instantiates a new Thread which takes
	 * over the communication between the modules.
	 */
	public void playSet() {
		boolean successfull = initializeInterface();
		if (!successfull) {
			JOptionPane.showInternalMessageDialog(null, "Interface wurde nicht erfolgreich initilisiert!");
		} else {
			resetAlgorithmManager();
			threadPlay = new ThreadPlay(interfaceManager, controllerKiKi, gameInfo, connection, algorithmManager,
					settings);
			threadPlay.start();
		}
	}

	/**
	 * 
	 * @param column
	 * @return int value of
	 * @throws Exception
	 *             if column is full already
	 */
	public void playTurnPlayerPlayer(int column) throws Exception {
		if (threadPlayerPlayer == null) {
			threadPlayerPlayer = new ThreadPlayerPlayer(controllerPlayerPlayer, gameInfo);
		}
		threadPlayerPlayer.playTurn(column);
	}

	/**
	 * This method does a reset of the ThreadPlayerPlayer-Object.
	 */
	public void resetThreadPlayerPlayer() {
		this.threadPlayerPlayer = null;
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

	/**
	 * This method fills the global variable images, which is a HashMap
	 */
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
	 * This method evaluates, if the dbConnection object can be used.<br>
	 * This method should be used before calling DB-methods to avoid
	 * SQL-Exceptions.
	 * 
	 * @return true, if the connection-Object can be used without problems.
	 */
	private boolean isConnectionFree() {
		if (threadPlay == null) {
			return true;
		} else {
			if (threadPlay.getState() == Thread.State.NEW || threadPlay.getState() == Thread.State.TERMINATED) {
				return true;
			} else {
				System.err.println("DB Connection belegt!");
				return false;
			}
		}
	}

	/**
	 * shuts down the current maanager and its threads ands initializes a new
	 * object.
	 */
	private void resetAlgorithmManager() {
		algorithmManager.shutdown();
		this.algorithmManager = new AlgorithmManager();
	}

	/**
	 * This method is called, when the Application is closing.<br>
	 * It closes all threads triggers other shutdown and closing methods.
	 */
	public void shutdownApplication() {
		if (algorithmManager != null) {
			algorithmManager.shutdown();
		}
		if (controllerKiKi != null) {
			controllerKiKi.exitApplication();
		}
		if (controllerReconstruct != null) {
			controllerReconstruct.exitApplication();
		}
		if (controllerPlayerKi != null) {
			controllerPlayerKi.exitApplication();
		}
	}
}
