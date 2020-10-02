package com.example.imageclassifierclient.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.imageclassifierclient.R;
import com.example.imageclassifierclient.service.RestService;
import com.example.imageclassifierclient.utility.ServiceAvailabilityChecker;

public class ClassListActivity extends AppCompatActivity {

	private ListView listView;

	private RestService restService;
	private ServiceAvailabilityChecker serviceAvailabilityChecker;

	private boolean isOnline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list);
		initService();
		checkApiAvailability();
		initElement();
		initUtility();
		getClasses();
	}

	private void checkApiAvailability() {
		isOnline = serviceAvailabilityChecker.checkApiAvailability();
	}

	public void getClasses() {
		if(isOnline) {
			final String[] response = new String[1];
			Thread t = new Thread(() -> restService.getClasses(response));
			t.start();
			try {
				t.join();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String[] classes = response[0].split(";");
			ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_layout, classes);
			listView.setAdapter(adapter);
		} else {
			Toast.makeText(
					this,
					getString(R.string.error_message),
					Toast.LENGTH_LONG
			).show();

			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}

	}

	private void initUtility(){
		serviceAvailabilityChecker = new ServiceAvailabilityChecker(this);
	}

	private void initElement() {
		listView = findViewById(R.id.classList);
	}

	public void initService() {
		Intent restIntent = new Intent(this, RestService.class);
		bindService(restIntent, restConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection restConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			RestService.LocalRestBinder restBinder = (RestService.LocalRestBinder) service;
			restService = restBinder.getService(ClassListActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
}