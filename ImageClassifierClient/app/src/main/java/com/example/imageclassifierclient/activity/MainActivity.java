package com.example.imageclassifierclient.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.imageclassifierclient.R;

public class MainActivity extends AppCompatActivity {
	private Button captureBtn, classesBtn, aboutBtn, exitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initElements();
		addListeners();
	}

	public void initElements() {
		captureBtn = findViewById(R.id.capture);
		classesBtn = findViewById(R.id.classes);
		aboutBtn = findViewById(R.id.about);
		exitBtn = findViewById(R.id.exit);
	}

	public void addListeners() {
		captureBtn.setOnClickListener(
				v -> {
					Intent intent = new Intent(MainActivity.this, ResultActivity.class);
					startActivity(intent);
				}
		);
		classesBtn.setOnClickListener(
				v -> {
					Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
					startActivity(intent);
				}
		);

		aboutBtn.setOnClickListener(
				v -> {

				}
		);

		exitBtn.setOnClickListener(
				v -> {
					moveTaskToBack(true);
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(1);
				}
		);
	}
}