package com.example.newsapplication.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.newsapplication.db.ArticleDatabase;
import com.example.newsapplication.model.ArticlesDataModel;


import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<ArticlesDataModel>> articles;
    public MainViewModel(@NonNull Application application) {
        super(application);
        ArticleDatabase database = ArticleDatabase.getInstance(getApplication());
        Log.d(TAG, "Actively retrieveing task from Database");
        articles = database.articleDao().loadAllArticles();
    }

    public LiveData<List<ArticlesDataModel>> getArticles() {
        return articles;
    }
}
