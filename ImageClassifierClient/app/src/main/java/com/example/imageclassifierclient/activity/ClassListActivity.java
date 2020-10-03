package com.example.imageclassifierclient.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imageclassifierclient.R;
import com.example.imageclassifierclient.service.RestService;
import com.example.imageclassifierclient.utility.ServiceAvailabilityChecker;

public class ClassListActivity extends AppCompatActivity {

	private Button backBtn;
	private LinearLayout classNameTextViewContainer;

	private RestService restService;
	private ServiceAvailabilityChecker serviceAvailabilityChecker;

	private boolean isOnline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list);
		initService();
		initUtility();
		checkApiAvailability();
		initElement();
		addListener();
	}

	private void checkApiAvailability() {
		isOnline = serviceAvailabilityChecker.checkApiAvailability();
	}

	public void getClasses() {
		if(isOnline) {
			final String[] response = new String[1];
			restService.getClasses(response);
			String[] classes = response[0].split(";");
			for(String className: classes) {
				TextView classNameTextView = new TextView(this);
				classNameTextView.setText(className);
				classNameTextView.setLayoutParams(
						new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
				classNameTextViewContainer.addView(classNameTextView);
			}
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

	public void addListener() {
		backBtn.setOnClickListener(
				v -> {
					Intent intent = new Intent(ClassListActivity.this, MainActivity.class);
					startActivity(intent);
				}
		);
	}

	private void initUtility(){
		serviceAvailabilityChecker = new ServiceAvailabilityChecker(this);
	}

	private void initElement() {
		classNameTextViewContainer = findViewById(R.id.classTextViewContainer);
		backBtn = findViewById(R.id.classListBackBtn);
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
			getClasses();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
}