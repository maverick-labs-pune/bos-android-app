/*
 * Copyright (c) 2019. Maverick Labs
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as,
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package net.mavericklabs.bos.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.mavericklabs.bos.utils.SharedPreferenceUtil;
import net.mavericklabs.bos.realm.RealmHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
//    private static final String BASE_URL = "http://192.168.0.105:8000";
    private static final String BASE_URL = "http://192.168.0.122:8000";
//    private static final String BASE_URL = "https://testapi.bridgesofsports.org";

    public static ApiInterface getApiInterface(Context context) {
        String token = "";
        String strToken = RealmHandler.getAccessToken();
        if (strToken != null)
            token = token + strToken;
        return createApiInterface(token,context);
    }

    private static ApiInterface createApiInterface(final String token, final Context context) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.readTimeout(90, TimeUnit.SECONDS);
        client.writeTimeout(90, TimeUnit.SECONDS);
        client.addInterceptor(loggingInterceptor);
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String locale = SharedPreferenceUtil.getLocale(context);
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Accept-Language", locale).build();

                return chain.proceed(request);
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .client(client.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(ApiInterface.class);
    }
}
