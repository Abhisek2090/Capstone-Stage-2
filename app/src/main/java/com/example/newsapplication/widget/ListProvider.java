package com.example.newsapplication.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.example.newsapplication.R;
import com.example.newsapplication.model.ArticlesDataModel;
import com.example.newsapplication.model.ArticlesResponseModel;
import com.example.newsapplication.utils.Constants;
import com.example.newsapplication.utils.GsonProvider;

import java.util.ArrayList;
import java.util.List;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<ArticlesDataModel> articles;
    private Context mContext = null;
    private ArticlesResponseModel articlesResponseModel;

    private static final String TAG = ListProvider.class.getSimpleName();

    public ListProvider(Context context, Intent intent) {
        Bundle extras =intent.getExtras();
        String personJsonString = String.valueOf(extras.get(Constants.ARTCILE));
        articlesResponseModel= GsonProvider.getGsonParser().fromJson(personJsonString, ArticlesResponseModel.class);
        mContext = context;
    }



    @Override
    public void onCreate() {
        if(articlesResponseModel != null)
        articles = articlesResponseModel.getArticles();

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        if(articles != null)
        articles.clear();
    }

    @Override
    public int getCount() {
        if(articles == null)
            return 0;
        return articles.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_single_text, articles.get(i).getTitle());
        return  remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
