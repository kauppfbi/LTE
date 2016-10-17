package  com.lte.models;

public class ServerMessage {
	private boolean unlocked;
	private String setStatus;
	private int opponentMove;
	private String winner;
	
	
	public ServerMessage(boolean unlocked, String setStatus, int opponentMove, String winner) {
		this.unlocked = unlocked;
		this.setStatus = setStatus;
		this.opponentMove = opponentMove;
		this.winner = winner;
	}
	public boolean isUnlocked() {
		return unlocked;
	}
	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}
	public String getSetStatus() {
		return setStatus;
	}
	public void setSetStatus(String setStatus) {
		this.setStatus = setStatus;
	}
	public int getOpponentMove() {
		return opponentMove;
	}
	public void setOpponentMove(int opponentMove) {
		this.opponentMove = opponentMove;
	}
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	
	
	@Override
	public String toString() {
		String out = "";
		out += "freigegeben: " + isUnlocked();
		out += "\nSet-Status: " + getSetStatus();
		out += "\nGegnerischer Zug: " + getOpponentMove();
		out += "\nGewinner: " + getWinner();
		
		return out;
	}
}
