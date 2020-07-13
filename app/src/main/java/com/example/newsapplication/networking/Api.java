package com.example.newsapplication.networking;

import com.example.newsapplication.model.ArticlesDataModel;
import com.example.newsapplication.model.ArticlesResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://newsapi.org/v2/";

    @GET("top-headlines")
    Call<ArticlesResponseModel> getHeadLines(@Query("country") String country ,
                                             @Query("apiKey") String apiKey,
                                             @Query("page") int page,
                                             @Query("pageSize") int pageSize);

}
