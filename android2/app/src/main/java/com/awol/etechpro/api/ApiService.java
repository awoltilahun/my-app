package com.awol.etechpro.api;

import com.awol.etechpro.model.AppLink;
import com.awol.etechpro.model.TechTip;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/techtips")
    Call<List<TechTip>> getAllTechTips();

    @GET("api/techtips/{id}")
    Call<TechTip> getTechTipById(@Path("id") Long id);

    @POST("api/techtips")
    Call<TechTip> createTechTip(@Body TechTip techTip);

    @PUT("api/techtips/{id}")
    Call<TechTip> updateTechTip(@Path("id") Long id, @Body TechTip techTip);

    @DELETE("api/techtips/{id}")
    Call<Void> deleteTechTip(@Path("id") Long id);

    @GET("api/techtips/search")
    Call<List<TechTip>> searchTechTips(@Query("keyword") String keyword);

    @GET("api/applinks")
    Call<List<AppLink>> getAllAppLinks();

    @GET("api/applinks/{id}")
    Call<AppLink> getAppLinkById(@Path("id") Long id);

    @POST("api/applinks")
    Call<AppLink> createAppLink(@Body AppLink appLink);

    @PUT("api/applinks/{id}")
    Call<AppLink> updateAppLink(@Path("id") Long id, @Body AppLink appLink);

    @DELETE("api/applinks/{id}")
    Call<Void> deleteAppLink(@Path("id") Long id);

    @GET("api/applinks/search")
    Call<List<AppLink>> searchAppLinks(@Query("keyword") String keyword);
}
