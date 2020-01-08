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

import net.mavericklabs.bos.model.Group;
import net.mavericklabs.bos.model.Measurement;
import net.mavericklabs.bos.model.Resource;
import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.model.NGO;
import net.mavericklabs.bos.model.Translation;
import net.mavericklabs.bos.model.User;
import net.mavericklabs.bos.model.UserReading;
import net.mavericklabs.bos.retrofit.custom.LoginRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("/mobile_login/")
    Call<LoginResponse> login(@Body LoginRequest obj);

    @POST("/refresh_mobile_token/")
    Call<LoginResponse> refreshToken();

    @GET("/translations/")
    Call<Translation> getTranslation();

    @FormUrlEncoded
    @POST("/users/{user_key}/reset_password/")
    Call<Void> resetPassword(@Path("user_key") String userKey, @Field("old_password") String oldPassword, @Field("new_password") String newPassword);

    @GET("/ngos/active_ngos/")
    Call<List<NGO>> getNGOs();

    @GET("/ngos/{ngo_key}/coach_registration_form/")
    Call<Resource> getCoachRegistrationForm(@Path("ngo_key") String ngoKey);

    @POST("/ngos/{ngo_key}/measurements_from_keys/")
    Call<List<Measurement>> getMeasurementsFromKeys(@Path("ngo_key") String ngoKey, @Body ArrayList<String> measurementKeys);

    @GET("/ngos/{ngo_key}/all_measurements/")
    Call<List<Measurement>> getAllMeasurements(@Path("ngo_key") String ngoKey);

    @GET("/users/{user_key}/resources/")
    Call<List<Resource>> getUserResources(@Path("user_key") String userKey);

    @GET("/users/{user_key}/groups/")
    Call<List<Group>> getGroups(@Path("user_key") String userKey);

    @GET("/users/{user_key}/athletes/")
    Call<List<User>> getAthletes(@Path("user_key") String userKey);

    @Headers("Content-Type: application/json")
    @POST("/readings/")
    Call<UserReading> createUserReading(@Body String body);
}
