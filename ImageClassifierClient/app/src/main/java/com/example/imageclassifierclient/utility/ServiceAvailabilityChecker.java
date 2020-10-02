package com.example.imageclassifierclient.utility;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imageclassifierclient.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceAvailabilityChecker {
	private AtomicBoolean isOnline;
	private AppCompatActivity activity;

	public ServiceAvailabilityChecker(AppCompatActivity activity) {
		this.activity = activity;
	}

	public boolean checkApiAvailability() {
		isOnline = new AtomicBoolean(false);
		Thread t = new Thread(() -> isOnline.set(isOnline()));
		t.start();
		try {
			t.join();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return isOnline.get();
	}

	private boolean isOnline() {
		try {
			int timeoutMs = 1500;
			Socket sock = new Socket();
			SocketAddress sockaddr = new InetSocketAddress(activity.getString(R.string.ip), activity.getResources().getInteger(R.integer.port));

			sock.connect(sockaddr, timeoutMs);
			sock.close();

			return true;
		} catch (IOException e) { return false; }
	}
}
