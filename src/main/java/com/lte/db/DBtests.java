package com.lte.db;

import java.sql.SQLException;
import java.util.Scanner;

public class DBtests {
	
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		DBconnection dbObj = new DBconnection();
		/*
		int settings[] = dbObj.startNewGame("devil", "X");
		
		System.out.println("gameID: " + settings[0] + "\n");
		System.out.println("setID: " + settings[1] + "\n");
		System.out.println("opponentID: " + settings[2] + "\n");
		*/
		int test[] = dbObj.getReplayTurns(24, 1);
		
		for (int i = 0; i < test.length; i++) {
			System.out.println(test[i]);
		}
		
		
		
		
		try {
			dbObj.stmt.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			dbObj.con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		DBconnection testInstanz = new DBconnection();
		System.out.println("Created DB connection instance");
		
		int userChoice = 5;
		
		while (userChoice != 0) {
			
			System.out.println("\n0: Exit");
			System.out.println("1: Insert new player");
			System.out.println("2: ...");
			System.out.println("\nPlease enter your choice: ");
			
			userChoice = sc.nextInt();
			
			switch (userChoice) {
			case 0:
				System.out.println("Going down");
				break;
			case 1:
				System.out.println("Please enter new name (without spaces):");
				String newName = sc.next();
				//int testid = testInstanz.insertPlayer(newName);
				//System.out.println("\nInserted new row, created PlayerID: " + testid + "\n");
				break;
			case 2:
				//testInstanz.
				break;
			default:
				System.out.println("Invalid");
				break;
			}
			
		}
		*/
		
		sc.close();
		
		
	}
	
}
