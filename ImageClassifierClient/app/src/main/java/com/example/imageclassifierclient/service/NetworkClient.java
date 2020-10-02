package com.example.imageclassifierclient.service;

import android.content.Context;

import com.example.imageclassifierclient.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//https://stackoverflow.com/questions/54586960/retrofit-post-request-with-form-data
public class NetworkClient {
	private static Retrofit retrofit;
	public static Retrofit getRetrofitClient(Context context) {
		if (retrofit == null) {
			OkHttpClient okHttpClient = new OkHttpClient.Builder()
					.build();
			retrofit = new Retrofit.Builder()
					.baseUrl(context.getString(R.string.rest_uri))
					.client(okHttpClient)
					/*https://stackoverflow.com/questions/32617770/how-to-get-response-as-string-using-retrofit-without-using-gson-or-any-other-lib*/
					.addConverterFactory(ScalarsConverterFactory.create())
					.build();
		}
		return retrofit;
	}
}
