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
public class Agent {

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

	public Agent(DBconnection connection) {
		this.connection = connection;
	}

	public void spiele() {

		// ********* instanciate all controllers ****************
		long now = System.currentTimeMillis();

		// instanciate interface

		// interfaceManager = new FileIM("C:/Users/Florian/Desktop/4G_Test", 'o');
		boolean successfull = initializeInterface();
		if (!successfull)
			JOptionPane.showInternalMessageDialog(null, "Interface wurde nicht erfolgreich initilisiert!");

		// lade KI
		algorithmManager = new AlgorithmusManager();

		// lade DB Controller
		int ids[] = connection.startNewGame(gameInfo.getOpponentName(), "X");

		// prepare gameInfo
		gameInfo.setGameID(ids[0]);
		gameInfo.setSetID(ids[1]);
		gameInfo.setOpponentID(ids[2]);

		gameInfo.setGameInProgress(true);

		// lade Spielstand-Logik
		Spielstand currentGameScore = new Spielstand();
		currentGameScore.initialisiere();

		ServerMessage message = interfaceManager.receiveMessage();
		int opponentMove = message.getOpponentMove();
		boolean startingRound = false;
		//set starting Player
		if(opponentMove == -1){
			//wir starten
			gameInfo.setNextPlayer('X');
		} else {
			gameInfo.setNextPlayer('O');
			startingRound = true;
		}

		System.out.println("Initialiseren hat " + String.valueOf(System.currentTimeMillis() - now) + " ms gedauert!");

		while (true) {
			// ***** Gegner spielt Zug ******
			if (gameInfo.getNextPlayer() == 'O') {
				System.out.println("Gegner spielt!");
				// -Spiele Gegnerzug im Spielstand
				try {
					if (startingRound) {
						startingRound = false;
					} else {
						message = interfaceManager.receiveMessage();
					}
					// - Spiel entschieden/gewonnen
					if (!message.getWinner().equals("offen")) {
						break;
					}
					opponentMove = message.getOpponentMove();

					currentGameScore.spiele(opponentMove, 'O');

					// visualisiere in GUI
					int row = currentGameScore.getZeile(opponentMove);
					controller1.fill(opponentMove, row, gameInfo.getNextPlayer(), false);

					// Log turn in DB
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "O", opponentMove);

					gameInfo.setNextPlayer('X');
					currentGameScore.print();
					continue;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// ***** wir spielen Zug *****
			else if (gameInfo.getNextPlayer() == 'X') {				
				System.out.println("Wir spielen");
				try {
					// berechne n�chsten Zug - KI gibt Spalte zur�ck
					int nextMove = algorithmManager.ParallelAlphaBeta(currentGameScore.getSpielfeld(), 10, 'X', 'O');

					// sende n�chsten Zug an Server
					interfaceManager.sendMove(nextMove);

					currentGameScore.spiele(nextMove, 'X');

					// visualisiere in GUI
					int row = currentGameScore.getZeile(nextMove);
					controller1.fill(nextMove, row, gameInfo.getNextPlayer(), false);

					// log turn in DB
					connection.pushTurn(gameInfo.getGameID(), gameInfo.getSetID(), "X", nextMove);

					gameInfo.setNextPlayer('O');
					currentGameScore.print();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} // endwhile

		algorithmManager.shutdown();
		
		// ****** Spiel ist entschieden *******

		// TODO Zuordnung von X/O zu Teamnamen
		System.out.println("Spieler: " + message.getWinner() + " hat gewonnen");

		// - Rückgabe der gewonnen Kombination aus dem Spieldstand int[3][1] ->
		// [Pos][Spalte] / [Pos][Zeile]
		// System.out.println(aktuellerSpielstand.woGewonnen());

		//controller1.gameOver();
		// TODO gameOver(String winnerTeam, int[] winnerCombo);
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
	public ResultSet getRecGameInfo(){
		System.out.println("agent rec");
		ResultSet res = connection.getGames();
		return res;
	}
	
	// get setnumbers for choice in reconstruction:
	public int getRecSetNumber(int gameID){
		int setNumber = connection.getNumberOfSetsInGame(gameID);
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
