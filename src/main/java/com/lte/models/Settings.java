package  com.lte.models;


import com.lte.interfaces.InterfaceManager;



/**
 * 
 * @author Florian & Fabian
 * Object Settings: saves the current Game-information
 *
 */

public class Settings {

	private double calculationTime;
	private String contactPath;
	// initialization with pusher-interface as the default interface
	private String interfaceType;
	private char serverChar;
	private String [] credentials;
	
	public Settings(double calculationTime, String contactPath, String interfaceType, char serverChar) {
		this.calculationTime = calculationTime;
		this.contactPath = contactPath;
		this.interfaceType = interfaceType;
		this.serverChar = serverChar;
	}

	public Settings(){
		//default values
		this.calculationTime = 0.5;
		this.interfaceType = InterfaceManager.EVENT_TYPE;
		this.serverChar = 'n';
	}

	
	//Getter and Setter methods
	public double getCalculationTime() {
		return calculationTime;
	}

	public void setCalculationTime(double calculationTime) {
		this.calculationTime = calculationTime;
	}

	public String getContactPath() {
		return contactPath;
	}

	public void setContactPath(String contactPath) {
		this.contactPath = contactPath;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

		
	public char getServerChar() {
		return serverChar;
	}

	public void setServerChar(char serverChar) {
		this.serverChar = serverChar;
	}	

	public String[] getCredentials() {
		return credentials;
	}

	public void setCredentials(String[] credentials) {
		this.credentials = credentials;
	}

	
	/**
	 * checks if the settings are completed
	 * @return boolean, wheter all attributes are set
	 */
	public boolean isCompleted(){
		boolean completed = false;
		//Pusher-Interface: interfaceType and Calculating-Time
		//File-Interface: InterfaceType, Calculating-Time, Contact-Path and ServerChar
		if (interfaceType == InterfaceManager.EVENT_TYPE || interfaceType == InterfaceManager.EVENT_TYPE_JSON){
			completed = true;
		}
		else if(interfaceType == InterfaceManager.FILE_Type){
			if(contactPath != null && (!contactPath.equals("")) && serverChar != 'n'){
				completed = true;
			} else {
				System.err.println("Settings not completed!");
				System.out.println(this);
			}
		}
		return completed;
	}

	/**
	 * toString method
	 */
	@Override
	public String toString() {
		return "Settings ===============\ncalculationTime=" + calculationTime + ", \ncontactPath=" + contactPath + ", \ninterfaceType="
				+ interfaceType + ", \nserverChar=" + serverChar;
	}	
}