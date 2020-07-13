package com.example.newsapplication.db;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.newsapplication.model.ArticlesDataModel;
import com.example.newsapplication.utils.Constants;

import java.util.List;

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ARTICLE)
    LiveData<List<ArticlesDataModel>> loadAllArticles();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(ArticlesDataModel article);

    @Delete
    void removeFromFav(ArticlesDataModel article);

}
