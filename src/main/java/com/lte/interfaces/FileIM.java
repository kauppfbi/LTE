package com.lte.interfaces;

import com.lte.models.ServerMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Interface Manager for File Interface<br>
 * implements InterfaceManager
 * 
 * @author kauppfbi
 *
 */
public class FileIM implements InterfaceManager {

	private String path;
	private String filename;
	private char player;

	/**
	 * Constructor<br>
	 * It requires the contactPath as String and the assigned player as a char.
	 * @param path - String for contact path
	 * @param player - char for our assigned player-char
	 */
	public FileIM(String path, char player) {
		this.path = path + "/";
		this.player = player;
		// filename automatisch festlegen
		// Spieler O oder x
		this.filename = "spieler" + player + "2server.txt";
	}

	@Override
	public ServerMessage receiveMessage() {
		String fileName = "server2spieler" + player + ".xml";

		ServerMessage serverMessage = null;
		while (true) {
			try {
				// System.out.println("wait for xml: " + path + fileName);
				serverMessage = readXML(path + fileName);
				if (serverMessage != null) {
					break;
				}
			} catch (IOException e) {
				try {
					// System.err.println("ServerXML nicht gefunden!");
					// e.printStackTrace();
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return serverMessage;
	}

	@Override
	public void sendMove(int column) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(path + filename, "UTF-8");
			writer.print(String.valueOf(column));
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			new File(path + filename);
			try {
				writer = new PrintWriter(path + filename, "UTF-8");
				writer.print(String.valueOf(column));
				writer.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

		} finally {
			if (writer != null)
				writer.close();
		}

	}

	/**
	 * This method takes over to read the xml-File from the server.
	 * @param filePath
	 * @return a serverMessage object
	 * @throws IOException
	 */
	private ServerMessage readXML(String filePath) throws IOException {

		Document doc = null;
		ServerMessage message = null;
		File f = new File(filePath);

		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(f);

			// Wurzelelement holen
			Element root = doc.getRootElement();

			// Werte auslesen
			boolean unlocked = Boolean.getBoolean(root.getChild("freigabe").getValue());
			String setStatus = root.getChild("satzstatus").getValue();
			int opponentMove = Integer.parseInt(root.getChild("gegnerzug").getValue());
			String winner = root.getChild("sieger").getValue();

			// neues ServerMessage Objekt erstellen
			message = new ServerMessage(unlocked, setStatus, opponentMove, winner);
			// System.out.println("Message:\n" + message);

			f.delete();

		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return message;
	}
}
