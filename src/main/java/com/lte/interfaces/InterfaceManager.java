package  com.lte.interfaces;

import com.lte.models.ServerMessage;

/**
 *  
 * @author kauppfbi
 *
 */
public interface InterfaceManager {
	public static final String EVENT_TYPE = "event";
	public static final String EVENT_TYPE_JSON = "event-json";
	public static final String FILE_Type = "file";
		
	public ServerMessage getLastMessage();

	//public void setLastMessage(ServerMessage lastMessage);

	public ServerMessage receiveMessage ();
	
	public void sendMove(int column);
	
}
