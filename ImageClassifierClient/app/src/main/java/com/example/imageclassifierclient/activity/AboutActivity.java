package com.example.imageclassifierclient.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.imageclassifierclient.R;

public class AboutActivity extends AppCompatActivity {

	private Button backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		addListener();
	}

	private void addListener() {
		backBtn = findViewById(R.id.aboutBtnBack);
		backBtn.setOnClickListener(
				v -> {
						Intent intent = new Intent(AboutActivity.this, MainActivity.class);
						startActivity(intent);
				}
		);
	}
}