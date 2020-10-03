package com.example.imageclassifierclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imageclassifierclient.R;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RestService extends Service {
	private final IBinder binder = new LocalRestBinder();

	private FlaskServerApi flaskServerApi;

	private AppCompatActivity activity;

	public RestService() {

	}

	public class LocalRestBinder extends Binder {
		public RestService getService(AppCompatActivity resultActivity) {
			Retrofit retrofit = NetworkClient.getRetrofitClient(RestService.this);
			flaskServerApi = retrofit.create(FlaskServerApi.class);
			setResultActivity(resultActivity);
			return RestService.this;
		}
	}

	public void getPrediction(final TextView resultTextView, final TextView confidencePercentTextView, File imageToPredict) {
		RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), imageToPredict);

		MultipartBody.Part part = MultipartBody.Part.createFormData("image", imageToPredict.getName(), fileReqBody);
		RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

		Call<String> call = flaskServerApi.getPrediction(part, description);

		call.enqueue(
				new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						if(!response.isSuccessful()) {
							Toast.makeText(activity,
									activity.getString(R.string.error_message) + response.code(),
									Toast.LENGTH_LONG).show();
						} else {
							String prediction = response.body();

							String[] splittedPrediction = prediction.split(";");
							String classLabel = splittedPrediction[0];
							String confidence = splittedPrediction[1];
							String confidencePercentToShow = formatConfidence(confidence);

							resultTextView.setText(classLabel);
							confidencePercentTextView.setText(confidencePercentToShow);
						}
					}

					@Override
					public void onFailure(Call<String> call, Throwable t) {
						Toast.makeText(activity,
								activity.getString(R.string.error_message),
								Toast.LENGTH_LONG).show();
					}
				});
	}

	public String formatConfidence(String confidence) {
		int confidencePercent = (int) Math.round(Double.parseDouble(confidence) * 100);
		return  String.valueOf(confidencePercent) + '%';
	}

	public void getCredentials(final String[] credentials)  {
		Call<String> call = flaskServerApi.getCredentials();
		call.enqueue(
				new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						if(!response.isSuccessful()) {
							Toast.makeText(activity,
									activity.getString(R.string.error_message) + response.code(),
									Toast.LENGTH_LONG).show();
						} else {
							credentials[0] = response.body();
						}
					}

					@Override
					public void onFailure(Call<String> call, Throwable t) {
					}});
	}

	public void getClasses(final String[] classes)  {
		Call<String> call = flaskServerApi.getClasses();
		Thread t = new Thread(() -> {
			try {
				Response<String> response = call.execute();
				classes[0] = response.body();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


//		call.enqueue(
//				new Callback<String>() {
//					@Override
//					public void onResponse(Call<String> call, Response<String> response) {
//						if(!response.isSuccessful()) {
//							Toast.makeText(activity,
//									activity.getString(R.string.error_message) + response.code(),
//									Toast.LENGTH_LONG).show();
//						} else {
//							classes[0] = response.body();
//						}
//					}
//
//					@Override
//					public void onFailure(Call<String> call, Throwable t) {
//					}});


	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void setResultActivity(AppCompatActivity resultActivity) {
		this.activity = resultActivity;
	}
}
