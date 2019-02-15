package com.protobender.artlibary.domain;

import com.protobender.artlibary.model.entity.Result;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Services {

    //String MAIN_URL = "http://protobender.000webhostapp.com/ArtLibrary/public/";
    String MAIN_URL = "http://192.168.1.7/ArtLibrary/public/";

    @GET("login/email/{email}/password/{password}")
    Call<Result> login(@Path("email") String email, @Path("password") String password);

    @FormUrlEncoded
    @POST("users")
    Call<Result> setUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("number") String number,
            @Field("address")String address);

    @Multipart
    @POST("artworks")
    Call<Result> setArtwork(@Part("artworkName") RequestBody artworkName,
                            @Part("author") RequestBody author,
                            @Part("date") RequestBody date,
                            @Part("description") RequestBody description,
                            @Part("deviceName") RequestBody deviceName,
                            @Part("userId") RequestBody userId,
                            @Part("artworkImage\"; filename=\"artworkImage.jpg\" ") RequestBody artworkImage);

    @GET("artworks")
    Call<Result> getArtworks();

    @GET("artworks/artworkName/{artworkName}")
    Call<Result> getArtworkByName(@Path("artworkName") String artworkName);

    @GET("artworks/userId/{userId}")
    Call<Result> getArtworkByUser(@Path("userId") String userId);

    @GET("artworks/artworkId/{artworkId}")
    Call<Result> updateArtwork(@Path("artworkId") String artworkId);

    @DELETE("artworks/artworkId/{artworkId}")
    Call<Result> deleteArtwork(@Path("artworkId") int artworkId);

}
