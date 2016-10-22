package  com.lte.models;


import com.lte.interfaces.InterfaceManager;



/**
 * 
 * @author Florian & Fabian
 *
 *         Objekt Settings: speichert die aktuellen gespeicherten
 *         Spielinformationen
 */

public class Settings {

	private double calculationTime;
	private String contactPath;
	// Initialisiert mit Pusher, weil das Standard-ausgewaehlt ist im Controller1
	private String interfaceType;
	private char serverChar;
	
	
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

	/**
	 * 
	 * @return boolean, wheter all attributes are set
	 */
	public boolean isCompleted(){
		boolean completed = false;
		//Logik hier
		//Wenn Pusher-Schnittstelle gew�hlt reichen, interfaceTyp und Rechenzeit -> �ber Konstruktor sichergestellt
		//bei Dateischnittstelle m�ssen zus�tzlich contactPath und ServerChar besetzt werden
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

	@Override
	public String toString() {
		return "Settings ===============\ncalculationTime=" + calculationTime + ", \ncontactPath=" + contactPath + ", \ninterfaceType="
				+ interfaceType + ", \nserverChar=" + serverChar;
	}	
}