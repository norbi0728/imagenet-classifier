package com.example.imageclassifierclient.service;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface FlaskServerApi {

	@Multipart
	@POST("prediction")
	Call<String> getPrediction(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

	@GET("credentials")
	Call<String> getCredentials();

	@GET("classes")
	Call<String> getClasses();
}
