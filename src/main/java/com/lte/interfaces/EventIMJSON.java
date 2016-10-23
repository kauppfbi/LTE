package com.lte.interfaces;

import com.lte.models.ServerMessage;
import org.json.*;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;

public class EventIMJSON implements InterfaceManager {

	private PusherOptions options;
	private Pusher pusher;
	private PrivateChannel channel;
	private ServerMessage serverMessage;
	
	public EventIMJSON(){
		this.options = new PusherOptions();
		options.setAuthorizer(new Authorizer() {
			@Override
			public String authorize(String channel, String socketId) throws AuthorizationFailureException {
				com.pusher.rest.Pusher pusher = new com.pusher.rest.Pusher("257244", "9f51aecddb01d8fc6a81",
						"0f543c2f8faab3c052ef");
				String response = pusher.authenticate(socketId, channel);
				System.out.println(response);
				return response;
			}
		});

		this.pusher = new Pusher("9f51aecddb01d8fc6a81", options);

		pusher.connect();

		this.channel = pusher.subscribePrivate("private-channel");
		
		try {

			channel.bind("MoveToAgent", new PrivateChannelEventListener() {
				@Override
				public void onSubscriptionSucceeded(String s) {
					System.out.println(s);
					System.out.println("onSubscriptionSucceeded()");
				}

				@Override
				public void onAuthenticationFailure(String s, Exception e) {
					System.out.println(s);
					System.out.println("onSubscriptionFailure()");
					e.printStackTrace();
				}

				@Override
				public void onEvent(String channelName, String eventName, final String message) {
					System.out.println(message);
					setServerMessage(message);
					
				}
			});
		} catch (Exception e) {
			System.err.println("Exception thrown in binding process!");
			e.printStackTrace();
		}
	}
	
	@Override
	public ServerMessage getLastMessage() {
		return serverMessage;
	}


	@Override
	public ServerMessage receiveMessage() {
		ServerMessage localMessage; 
		
		while(true){
			if(serverMessage != null){
				localMessage = serverMessage;
				serverMessage = null;
				break;
			}
			else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return localMessage;
	}

	@Override
	public void sendMove(int column) {
		channel.trigger("client-event", "{\"move\": \"" + column + "\"}");
		
	}

	
	// prepares received message 
	private void setServerMessage(String message){
		
		JSONObject jsonMessage = new JSONObject(message);

		JSONObject content = jsonMessage.getJSONObject("content");

		// Werte auslesen
		boolean unlocked = content.getBoolean("satzstatus");
		String setStatus = content.getString("satzstatus");
		int opponentMove = content.getInt("gegnerzug");
		String winner = content.getString("sieger");

		// neues ServerMessage Objekt erstellen
		serverMessage = new ServerMessage(unlocked, setStatus, opponentMove, winner);
		
	}

}
