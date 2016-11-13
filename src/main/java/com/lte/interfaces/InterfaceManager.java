package  com.lte.interfaces;

import com.lte.models.ServerMessage;

/**
 * Interface for our <i>interface-classes</i> which should organize the communication with the server.
 * 
 * @author kauppfbi
 *
 */
public interface InterfaceManager {
	//constants
	public static final String EVENT_TYPE = "event";
	public static final String EVENT_TYPE_JSON = "event-json";
	public static final String FILE_Type = "file";
		

	/**
	 * This method reads the (last) transmitted message from the server.
	 * @return ServerMessage-object (all information from server in one java object)
	 */
	public ServerMessage receiveMessage ();
	
	/**
	 * This method sends our next move to the server.
	 * @param column - int value of the column index
	 */
	public void sendMove(int column);
	
}
