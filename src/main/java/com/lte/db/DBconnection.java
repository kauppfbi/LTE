package com.lte.db;

import java.sql.*;
import java.util.ArrayList;

import com.lte.models.GameDB;
import com.lte.models.SetDB;
import com.lte.models.DBscoreboard;

public class DBconnection {

	// Create db connection and statement object
	// Available for all methods
	public Connection con = null;
	public Statement stmt = null;

	public DBconnection() {

		// load jdbc driver
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Database driver class not found");
		}

		// Connect connection object to file db
		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:db/game;shutdown=true;hsqldb.write_delay=false;",
					"root", "root");
			System.out.println("LOG: created db connection");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Couldn't get connection to sb through jdbc");
		}

	}

	/**
	 * Creates all database entries needed for a new game. This is the first
	 * method to call if you want to start a new game.
	 * 
	 * @param opponentName
	 * @param startingPlayer
	 * @return integer array with game information <br>
	 *         array[0] = gameID <br>
	 *         array[1] = setID <br>
	 *         array[2] = opponentID
	 * 
	 */
	public int[] startNewGame(String opponentName, String startingPlayer) {

		int opponentID = 0;
		int gameID = 0;
		int setID = 0;

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Couldn't create statement");
		}

		// create player entry or get id of existing player entry
		String sql = "SELECT * FROM \"PUBLIC\".\"OPPONENT\" WHERE OPPONENTNAME = '" + opponentName + "'";

		try {
			ResultSet res = stmt.executeQuery(sql);

			if (!res.isBeforeFirst()) {
				System.out.println("LOG: opponent not found, creating new opponent");
				opponentID = insertOpponent(opponentName);
			} else {
				System.out.println("LOG: opponent already existed, skipping creation");
				if (res.next()) {
					opponentID = res.getInt(1);
				}
				res.close();
			}
		} catch (SQLException e) {
			System.out.println("Couldn't create new opponent or get opponentid of existing opponent");
		}

		// create new game entry with opponentid as opponent
		sql = "INSERT INTO \"PUBLIC\".\"GAME\" ( \"OPPONENTID\" ) VALUES ( " + opponentID + " )";
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: Created game entry in db table");
		} catch (SQLException e) {
			System.out.println("Couldn't create new game");
		}

		sql = "SELECT max(GAMEID) from PUBLIC.GAME";
		try {

			ResultSet res = stmt.executeQuery(sql);
			System.out.println("LOG: got gameid of newly created game");
			if (res.next()) {
				gameID = res.getInt(1);
			}
			res.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't get last gameid in table");
		}

		// create 1st gameset referencing the id of the newly created game,
		// starting player O or X
		sql = "INSERT INTO \"PUBLIC\".\"GAMESET\" ( \"GAMEID\", \"STARTINGPLAYER\", \"POINTSOWNBEFORESET\", \"POINTSOPPONENTBEFORESET\" ) VALUES ( "
				+ gameID + ", '" + startingPlayer + "', 0, 0)";

		try {
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't create first gameset");
		}

		// get id of newly created set
		sql = "SELECT max(SETID) from PUBLIC.GAMESET";
		try {

			ResultSet res = stmt.executeQuery(sql);

			if (res.next()) {
				setID = res.getInt(1);
			}
			res.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't get last setid in gameset table");
		}

		int gameInfo[] = new int[3];
		gameInfo[0] = gameID;
		gameInfo[1] = setID;
		gameInfo[2] = opponentID;

		return gameInfo;
	}

	/**
	 * Creates new entry in the database for a new set after the previous ended.
	 * Updates the game entry in the database with the score after the previous
	 * set.
	 * 
	 * @param GameID
	 * @param CurrentGamePointsOwn
	 * @param CurrentGamePointsOpponent
	 * @return ID of new gameset
	 */
	public int createNewSet(int iGameID, int CurrentGamePointsOwn, int CurrentGamePointsOpponent) {
		// Set starting player to opposite player than set before
		// Update game entry with latest score

		int setID = 0;
		int latestSetID = 0;
		String newStartingPlayer = "";
		String lastStartingPlayer = "";

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Couldn't create statement");
		}

		// get latest set from selected gameID
		String sql = "SELECT max(SETID) from PUBLIC.GAMESET WHERE GAMEID = " + iGameID;

		try {
			ResultSet res = stmt.executeQuery(sql);

			if (res.next()) {
				latestSetID = res.getInt(1);
			}
			res.close();

		} catch (SQLException e) {
			System.out.println("LOG: SQL Error");
		}

		// get starting player of last set in game
		sql = "SELECT StartingPlayer from PUBLIC.GAMESET where SETID = " + latestSetID;
		try {
			ResultSet res = stmt.executeQuery(sql);
			if (res.next()) {
				lastStartingPlayer = res.getString(1);
			}
			res.close();
		} catch (SQLException e) {
			System.out.println("LOG: SQL Error");
		}

		if (lastStartingPlayer == "X") {
			newStartingPlayer = "O";
		} else if (lastStartingPlayer == "O") {
			newStartingPlayer = "X";
		}

		// Create new entry
		sql = "INSERT INTO \"PUBLIC\".\"GAMESET\" ( \"GAMEID\", \"STARTINGPLAYER\", \"POINTSOWNBEFORESET\", \"POINTSOPPONENTBEFORESET\" ) VALUES ( "
				+ iGameID + ", '" + newStartingPlayer + "', " + CurrentGamePointsOwn + ", " + CurrentGamePointsOpponent
				+ ")";

		try {
			stmt.executeQuery(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("LOG: Couldn't create new set!");
		}

		// Get id of new gameset entry
		sql = "SELECT max(SETID) from PUBLIC.GAMESET";
		try {

			ResultSet res = stmt.executeQuery(sql);

			if (res.next()) {
				setID = res.getInt(1);
			}
			res.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return setID; // SetID
	}

	/**
	 * Saves a turn of a specific game, set and player into the database.
	 * 
	 * @param GameID
	 * @param SetID
	 * @param Player
	 * @param Column
	 */
	public void pushTurn(int GameID, int SetID, String Player, int Column) {

		// int turnID = 0;

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Couldn't create statement");
		}

		// get latest set from selected gameID
		String sql = "INSERT INTO \"PUBLIC\".\"TURN\" ( \"GAMEID\", \"SETID\", \"TURN\", \"COLUMN\" ) VALUES ( "
				+ GameID + ", " + SetID + ", '" + Player + "', " + Column + ")";
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: inserted new turn in db table turn");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates Winner of set after the completion of a set
	 * 
	 * @param SetID
	 * @param Winner
	 */
	public void updateWinnerOfSet(int SetID, String Winner) {
		String sql = "UPDATE \"PUBLIC\".\"GAMESET\" SET (\"WINNER\") = (" + Winner + ") WHERE \"SETID\" = " + SetID;
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: Updated winner of last set");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets game result (score) in db table after game ends
	 * 
	 * @param GameID
	 * @param PointsOwn
	 * @param PointsOpponent
	 */
	public void updateScoreOfGame(int GameID, int PointsOwn, int PointsOpponent, String Winner) {
		String sql = "UPDATE \"PUBLIC\".\"GAME\" SET (\"POINTSOWN\", \"POINTSOPPONENT\", \"WINNER\") = (" + PointsOwn
				+ ", " + PointsOpponent + ", " + Winner + ") WHERE \"GAMEID\" = " + GameID;
		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: Set game score");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Call to get all games of the database with additional info
	 * 
	 * @return GameDB array with information about all games in the db. Based on
	 *         GameDB model Column 1 = Opponent name <br>
	 *         Column 2 = PlayTime <br>
	 *         Column 3 = GameID <br>
	 *         Column 4 = PointsOwn <br>
	 *         Column 5 = PointsOpponent <br>
	 *         Column 6 = Winner
	 */
	public GameDB[] getGames() {
		ResultSet res = null;
		GameDB[] gamesInfo = null;
		GameDB gameInfo = null;
		int totalGames = 0;

		int mGameID = 0;
		String mOpponentName;
		String mPlayTime;
		int mNumberOfSets;

		int counter = 0;
		String sql = "Select GAMEID, OPPONENTNAME, PLAYTIME, POINTSOWN, POINTSOPPONENT, WINNER from PUBLIC.GAME, PUBLIC.OPPONENT where GAME.OPPONENTID = OPPONENT.OPPONENTID";

		try {
			// stmt = con.createStatement();
			PreparedStatement stmt = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = stmt.executeQuery();
			System.out.println("LOG: Got all games");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// res = stmt.executeQuery(sql);

			if (res.next()) {
				res.last();
				totalGames = res.getRow();
				res.beforeFirst();
				gamesInfo = new GameDB[totalGames];

				// Iterate over ResultSet and move GameInfo to GameDB object
				while (res.next()) {
					mGameID = res.getInt(1);
					mOpponentName = res.getString(2);
					mPlayTime = res.getString(3);
					mNumberOfSets = getNumberOfSetsInGame(mGameID);

					gameInfo = new GameDB(mGameID, mOpponentName, mPlayTime, mNumberOfSets);
					gamesInfo[counter] = gameInfo;
					counter++;
				}
			} else {
				System.out.println("LOG: no games found");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gamesInfo;
	}

	/**
	 * Method returns GameDB object with all the information needed to fill 2nd
	 * dropdown and replay a complete set for a selected gameID.
	 * 
	 * @param GameID
	 * @return GameDB object with the following attributes: <br>
	 *         SetID <br>
	 *         PointsOwnBeforeSet <br>
	 *         PointsOpponentBeforeSet <br>
	 *         Winner <br>
	 *         ReplayTurns as int array with the following specification: <br>
	 *         array[0] = 0 if we started, 1 of opponent started <br>
	 *         array[1..n] = columns of turns
	 */
	public SetDB[] getSetInfos(int GameID) {
		ResultSet res = null;
		SetDB[] gamesInfo = null;
		SetDB gameInfo = null;
		int totalSets = 0;

		int setID;
		int pointsOwn;
		int pointsOpponent;
		String winner;
		int[] replayTurns;

		int counter = 0;

		String sql = "Select SETID, POINTSOWNBEFORESET, POINTSOPPONENTBEFORESET, WINNER from PUBLIC.GAMESET WHERE GAMEID = "
				+ GameID + "ORDER BY SETID ASC";

		try {
			PreparedStatement stmt2 = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			res = stmt2.executeQuery();
			if (res.next()) {
				res.last();
				totalSets = res.getRow();
				res.beforeFirst();
				gamesInfo = new SetDB[totalSets];

				// Iterate over ResultSet and move SetInfo to SetDB object
				while (res.next()) {

					setID = res.getInt(1);
					pointsOwn = res.getInt(2);
					pointsOpponent = res.getInt(3);
					winner = res.getString(4);
					replayTurns = getReplayTurns(GameID, counter + 1);

					gameInfo = new SetDB(setID, pointsOwn, pointsOpponent, winner, replayTurns);
					gamesInfo[counter] = gameInfo;
					counter++;
				}
			} else {
				System.out.println("LOG: no sets found");
			}

			System.out.println("LOG: Got all Sets");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gamesInfo;
	}

	/**
	 * Method returns all the data needed to build the scoreboard UI Only needs
	 * to be called by GUI/Gui manager
	 * 
	 * @return DBscoreboard array
	 */
	public DBscoreboard[] getScoreboard() {
		DBscoreboard opponentStats = null;
		ArrayList<String> names = new ArrayList<String>();
		DBscoreboard[] opponentsStats = null;

		// Get unique number of opponents for opponentsStats array size
		String sql = "SELECT DISTINCT COUNT(OPPONENTNAME) FROM PUBLIC.OPPONENT";
		try {
			ResultSet res = stmt.executeQuery(sql);
			System.out.println("LOG: Got unique number of opponents");
			if (res.next()) {
				opponentsStats = new DBscoreboard[res.getInt(1)];
			} else {
				System.out.println("LOG: No opponents received");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sql = "SELECT OPPONENTID, OPPONENTNAME, GAMEID, WINNER FROM PUBLIC.GAME, PUBLIC.OPPONENT WHERE GAME.OPPONENTID = OPPONENT.OPPONENTID";

		PreparedStatement stmt2;
		try {
			stmt2 = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet res = stmt2.executeQuery();

			while (res.next()) {
				opponentStats = new DBscoreboard();
				String opponentName = res.getString(2);
				int index = 0;
				int wins = 0;
				int loses = 0;
				int score = 0;

				if (names.contains(opponentName)) {
					// Get position in ArrayList
					index = names.indexOf(opponentName);
					// Get DBscoreboard object from DBscoreboard array at this
					// position
					opponentStats = opponentsStats[index];
					// Modify DBscoreboard object
					wins = opponentStats.getWins();
					loses = opponentStats.getLoses();
					score = opponentStats.getScore();

					if (res.getString(4).equals("X")) {
						// add 1 to loses
						loses++;
					} else if (res.getString(4).equals("O")) {
						// add 1 to wins
						wins++;
					}

					// recalc score
					score = wins - loses;
					opponentStats.setLoses(loses);
					opponentStats.setWins(wins);
					opponentStats.setScore(score);

					// push the updated object to the position in the array
					opponentsStats[index] = opponentStats;
				} else {
					names.add(opponentName);
					index = names.indexOf(opponentName);
					// create object
					opponentStats.setOpponentName(opponentName);
					if (res.getString(4).equals("X")) {
						opponentStats.setLoses(1);
						opponentStats.setWins(0);
					} else if (res.getString(4).equals("O")) {
						opponentStats.setWins(1);
						opponentStats.setLoses(0);
					}
					opponentStats.setScore(opponentStats.getWins() - opponentStats.getLoses());
					opponentsStats[index] = opponentStats;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Got scoreboard information");
		}

		return opponentsStats;

	}

	/**
	 * Inserts a new opponent into the database and checks if the opponent
	 * already exists
	 * 
	 * @param name
	 * @return opponentID of existing or new opponent
	 */
	private int insertOpponent(String name) {

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't create statement");
		}

		String sql = "INSERT INTO \"PUBLIC\".\"OPPONENT\" (\"OPPONENTNAME\" ) VALUES ('" + name + "')";

		try {
			stmt.executeQuery(sql);
			System.out.println("LOG: created new opponent entry in db table PLAYER");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: couldn't create new opponent in db");
		}

		sql = "SELECT max(OpponentID) from PUBLIC.Opponent";
		int id = 0;

		try {

			ResultSet res = stmt.executeQuery(sql);
			System.out.println("LOG: got new opponent from db table opponent");

			if (res.next()) {
				id = res.getInt(1);
			}
			res.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * Gets all turns of a specific gameset and returns it together with the
	 * starting player in that set.
	 * 
	 * @param GameID
	 * @param SetNumber
	 * @return integer array: <br>
	 *         array[0] = 0 if we started, 1 of opponent started <br>
	 *         array[1..n] = columns of turns
	 */
	private int[] getReplayTurns(int GameID, int SetNumber) {

		int totalRows = 0;
		int[] turns = null;
		int counter = 2;
		int startingPlayer = 0; // 0 = we started, 1 = opponent started
		int mapping[] = new int[getNumberOfSetsInGame(GameID)];
		int mappingCounter = 0;

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Couldn't create statement");
		}

		// Mapping von eingegebener Satznummer zu SatzID in DB
		String sql = "SELECT * FROM PUBLIC.GAMESET WHERE GAMEID = " + GameID;

		try {
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				mapping[mappingCounter] = res.getInt(1);
				mappingCounter++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Couldn't get sets of game");
		}

		PreparedStatement stmt2 = null;
		try {
			stmt2 = con.prepareStatement(
					"SELECT * FROM PUBLIC.TURN WHERE GAMEID = " + GameID + " AND SETID = " + mapping[SetNumber - 1],
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			ResultSet res = stmt2.executeQuery();

			if (res.next()) {
				res.last();
				totalRows = res.getRow();
				turns = new int[totalRows + 1];
				res.beforeFirst();

				// get starting player and first turn
				if (res.next()) {

					if (res.getString(4) == "X") {
						startingPlayer = 0;
					} else if (res.getString(4) == "O") {
						startingPlayer = 1;
					}

					turns[0] = startingPlayer;
					turns[1] = res.getInt(5);
				}

				while (res.next()) {
					turns[counter] = res.getInt(5);
					counter++;
				}
			}
			res.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return turns;
	}

	/**
	 * Returns number of sets in a specific game
	 * 
	 * @param GameID
	 * @return number of sets in game
	 */
	private int getNumberOfSetsInGame(int GameID) {
		int number = 0;

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("LOG: Couldn't create statement");
		}

		/*
		 * Mapping von eingegebener Satznummer zu SatzID in DB
		 */
		PreparedStatement stmt2 = null;
		try {
			stmt2 = con.prepareStatement("SELECT * FROM PUBLIC.GAMESET WHERE GAMEID = " + GameID,
					ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("LOG: couldn't get sets of game");
		}

		try {
			ResultSet res = stmt2.executeQuery();
			if (res.next()) {
				res.last();
				number = res.getRow();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Couldn't get sets of game");
		}

		return number;
	}
}
