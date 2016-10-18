package  com.lte.controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.lte.aiPar.AlgorithmusManager;
import com.lte.db.DBconnection;
import com.lte.gui.*;
import com.lte.interfaces.*;
import com.lte.models.*;


/**
 * main controller; coordinates data exchange and communication between all
 * interfaces or rather controllers.
 * 
 * @author kauppfbi
 *
 */
public class AgentRekonst extends Thread {

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

	public AgentRekonst(DBconnection connection) {
		this.connection = connection;
	}

	public void run() {

		//Hier rekonstruieren.....
		//Test
		
	}

	private boolean initializeInterface() {
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

	// for testing reasons
	public int[] getReplayTurns(int GameID, int setNumber) {
		int turns[] = connection.getReplayTurns(GameID, setNumber);
		return turns;
	}
	
	// get gameinfo for coice in reconstruction:
	public GameDB[] getRecGameInfo(){
		System.out.println("agent rec");
		GameDB[] res = connection.getGames();
		return res;
	}
	
	// get setnumbers for choice in reconstruction:
	public GameDB[] getRecSetNumber(int gameID){
		GameDB[] setNumber = connection.getSetInfos(gameID);
		return(setNumber);
	}
	
	// public static void main(String[] args) {
	// Agent agent = new Agent(new DBconnection());
	// agent.getReplayTurns(23, 1);
	// }

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
	
}
