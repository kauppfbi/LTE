package com.lte.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SimpleThread extends Thread {
	private int counter = 1;

	@Override
	public synchronized void run() {
		while (true) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			System.out.println(sdf.format(cal.getTime()) + ": SimpleThread-Counter: " + counter++);
			
			try {
				this.wait(1000);
			} catch (InterruptedException e) {
				try {
					this.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
