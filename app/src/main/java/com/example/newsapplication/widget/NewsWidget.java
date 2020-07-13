package com.example.newsapplication.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        if (articles != null) {
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
        if (articles == null) {
            String url = "http://newsapi.org/v2/top-headlines?country=in&apiKey=dd46f83634ed4aab9cfe86c3ee6052a5&page=1&pageSize=10";
            new getHeadlines().execute(url);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        addHeadlines(articlesResponseModel, context);
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
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewsWidget.class));

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



    public class getHeadlines extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 60000;
        public static final int CONNECTION_TIMEOUT = 60000;

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);


                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                articlesResponseModel = GsonProvider.getGsonParser().fromJson(result, ArticlesResponseModel.class);
                addHeadlines(articlesResponseModel, context);
            }
        }
    }
}

