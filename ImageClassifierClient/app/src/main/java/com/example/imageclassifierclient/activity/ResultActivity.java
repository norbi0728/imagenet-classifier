package com.example.imageclassifierclient.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imageclassifierclient.R;
import com.example.imageclassifierclient.service.RestService;
import com.example.imageclassifierclient.utility.EmailUtility;
import com.example.imageclassifierclient.utility.PhotoUtility;
import com.example.imageclassifierclient.utility.ServiceAvailabilityChecker;

import java.io.File;

public class ResultActivity extends AppCompatActivity {

    private PhotoUtility photoUtility;
    private EmailUtility emailUtility;
    private ServiceAvailabilityChecker serviceAvailabilityChecker;

    private RestService restService;

    private File capturedImage;
    private String[] credentials;
    private boolean isOnline;

    private Button backBtn, newCaptureBtn, feedbackBtn, sendBtn, disclaimerBackBtn;

    private TextView result, confidencePercent;
    private PopupWindow popupWindow;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initService();
        initElements();
        addListeners();
        initUtility();
        photoUtility.startImageCapture();
        checkApiAvailability();
    }

    private void addListeners() {
        newCaptureBtn.setOnClickListener(
                v -> {
                        checkApiAvailability();
                        photoUtility.startImageCapture();
                     }
        );

        backBtn.setOnClickListener(
                v -> {
                    Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );

        feedbackBtn.setOnClickListener(
                v -> {
                    setUpPopUp(v);

                    disclaimerBackBtn.setOnClickListener(
                            v12 -> popupWindow.dismiss()
                    );

                    sendBtn.setOnClickListener(
                            v1 -> {
                                sendFeedbackEmail();
                                popupWindow.dismiss();
                                showToast(getString(R.string.feedback_sent));
                            }
                    );
                }
        );

    }

    public void showToast(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    public void sendFeedbackEmail() {
        Thread t = new Thread(
                () -> {
                        String [] splittedCredentials = credentials[0].split(";");
                        String email = splittedCredentials[0];
                        String password = splittedCredentials[1];
                        emailUtility = new EmailUtility(email, password);
                        emailUtility.send(capturedImage, result.getText().toString(), confidencePercent.getText().toString(), String.valueOf(ratingBar.getRating()));
                }
        );

        t.start();
    }

    public void setUpPopUp(View v) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.disclaimer, null);

        popupWindow = new PopupWindow(popupView, 850, 820, true);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        sendBtn = popupView.findViewById(R.id.send);
        disclaimerBackBtn = popupView.findViewById(R.id.disclaimerBack);
    }

    public void checkApiAvailability(){
        isOnline = serviceAvailabilityChecker.checkApiAvailability();
    }

    public void initElements() {
        newCaptureBtn = findViewById(R.id.newCapture);
        backBtn = findViewById(R.id.back);
        feedbackBtn = findViewById(R.id.feedback);
        result = findViewById(R.id.result);
        ratingBar = findViewById(R.id.ratingBar);
        confidencePercent = findViewById(R.id.confidencePercent);
    }

    public void initUtility() {
        photoUtility = new PhotoUtility(
                getPackageManager(),
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                this
        );

        serviceAvailabilityChecker = new ServiceAvailabilityChecker(this);

        credentials = new String[1];
    }

    public void initService() {
        Intent restIntent = new Intent(this, RestService.class);
        bindService(restIntent, restConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isOnline) {
            feedbackBtn.setEnabled(true);
            capturedImage = photoUtility.getResultImage(requestCode, resultCode);

            restService.getPrediction(result, confidencePercent, capturedImage);
            showToast(getString(R.string.wait_for_response));
        } else {
            feedbackBtn.setEnabled(false);
            showToast(getString(R.string.error_message));
        }
    }

    private ServiceConnection restConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RestService.LocalRestBinder restBinder = (RestService.LocalRestBinder) service;
            restService = restBinder.getService(ResultActivity.this);
            restService.getCredentials(credentials);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

}