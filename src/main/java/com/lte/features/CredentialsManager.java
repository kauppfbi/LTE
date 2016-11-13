package com.lte.features;

import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The Credentials Manager Class manages pusher credentials in a json-object.
 * 
 * @author kauppfbi
 *
 */
public class CredentialsManager {

	/**
	 * call this method to receive the last used pusher credentials.
	 * 
	 * @return String Array of pusher Credentials
	 */
	public String[] readCredentials() {
		String jsonString = readFile("files/credentials.json");
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject jsonCredentials = jsonObject.getJSONObject("credentials");

		/**
		 * credentials[0] --> id credentials[1] --> key credentials[2] -->
		 * secret
		 */
		String[] credentials = new String[3];
		credentials[0] = jsonCredentials.getString("id");
		credentials[1] = jsonCredentials.getString("key");
		credentials[2] = jsonCredentials.getString("secret");

		return credentials;
	}

	/**
	 * Call this method to set the transfered credentials as last used.
	 * 
	 * @param credentials
	 */
	public void setCredentials(String[] credentials) {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonCredentials = new JSONObject();
		jsonCredentials.put("id", credentials[0]);
		jsonCredentials.put("key", credentials[1]);
		jsonCredentials.put("secret", credentials[2]);
		jsonObject.put("credentials", jsonCredentials);

		PrintWriter writer = null;
		try {
			writer = new PrintWriter("files/credentials.json", "UTF-8");
			writer.print(jsonObject);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * manages to read out the json object as file
	 * 
	 * @param filename
	 * @return String --> Contnent of the json-object
	 */
	private String readFile(String filename) {
		String content = null;
		File file = new File(filename);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}
}
