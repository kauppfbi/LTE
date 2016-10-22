package com.lte.interfaces;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;

import com.lte.models.ServerMessage;

/**
 * standard Event Interface Manager  
 * @author kauppfbi
 *
 */
public class EventIM implements InterfaceManager{

	private PusherOptions options;
	private Pusher pusher;
	private PrivateChannel channel;
	private ServerMessage serverMessage;
	
	public EventIM(){
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
					Thread.sleep(1000);
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
		
		//cuts start and end of messageString
		message = message.substring(12, message.length());
		message = message.substring(0, message.length()-2);
		
		//splits by '#'
		String [] split = message.split("#");

		//catch unlockedStatus
		boolean unlocked = Boolean.valueOf(split[0]);
		
		//catch setStatus
		split[1] = split[1].substring(1, split[1].length()-1);
		String setStatus = split[1];
		
		//catch opponentMove 
		split[2] = split[2].substring(1, split[2].length()-1);
		int opponentMove = Integer.parseInt(split[2]);
		
		split[3] = split[3].substring(1, split[3].length());
		String winner = split[3];
		
		serverMessage = new ServerMessage(unlocked, setStatus, opponentMove, winner);
	}
	
	public static void main(String[] args) {
		EventIM manager = new EventIM();
		
		for(int i = 0; i < 5; i++){
			System.out.println(manager.receiveMessage());
			manager.sendMove(2);
		}
		System.out.println();
	}
}
