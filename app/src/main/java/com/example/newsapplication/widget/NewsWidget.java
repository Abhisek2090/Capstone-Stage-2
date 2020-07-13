package com.example.newsapplication.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.newsapplication.R;
import com.example.newsapplication.activity.HeadlinesActivity;

import com.example.newsapplication.model.ArticlesResponseModel;
import com.example.newsapplication.networking.Api;
import com.example.newsapplication.utils.Constants;
import com.example.newsapplication.utils.GsonProvider;
import com.google.gson.Gson;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Implementation of App Widget functionality.
 */
public class NewsWidget extends AppWidgetProvider {

    private static final String TAG = NewsWidget.class.getSimpleName();

   private ArticlesResponseModel articlesResponseModel;
    private Context context;
    private static Gson gson;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId, ArticlesResponseModel articles) {

            this.context = context;

        if(articles != null) {
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            Bundle args = new Bundle();
            String personJsonString = GsonProvider.getGsonParser().toJson(articles);
            args.putString(Constants.ARTCILE, personJsonString);
            intent.putExtras(args);


            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.newswidget);
            views.setRemoteAdapter(R.id.widget_list_lv, intent);
            Intent startActivityIntent = new Intent(context, HeadlinesActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, 0);

            views.setOnClickPendingIntent(R.id.open_news_app_btn, pendingIntent);
            // Instruct the widget manager to update the widget
             appWidgetManager.updateAppWidget(appWidgetId, views);

        }
        if(articles == null)
        getHeadlines();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        addHeadlines(articlesResponseModel , context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void addHeadlines(ArticlesResponseModel articlesResponseModel, Context context) {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds( new ComponentName(context, NewsWidget.class));

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, articlesResponseModel);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //handles broadcast messages to the receiver.

    }

    private void getHeadlines() {
        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the api interface
        Api api = retrofit.create(Api.class);


        Call<ArticlesResponseModel> call = api.getHeadLines("in", Constants.API_KEY ,1, 10);

        call.enqueue(new Callback<ArticlesResponseModel>() {
            @Override
            public void onResponse(Call<ArticlesResponseModel> call, Response<ArticlesResponseModel> response) {
                if (response.body() != null) {
                    ArticlesResponseModel result = response.body();
                        articlesResponseModel = result;
                        addHeadlines( articlesResponseModel, context);

                }
            }

            @Override
            public void onFailure(Call<ArticlesResponseModel> call, Throwable t) {
                Log.i(TAG, t.toString());
            }

        });


    }
}

