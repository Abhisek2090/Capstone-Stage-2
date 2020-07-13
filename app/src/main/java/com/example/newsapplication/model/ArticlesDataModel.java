package com.example.newsapplication.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.newsapplication.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Entity(tableName = Constants.TABLE_NAME_ARTICLE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticlesDataModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int article_id;
    private String author;
    private String title;
    private String urlToImage;
    private String publishedAt;
    private String description;
    private boolean fav;

    public ArticlesDataModel(@JsonProperty("author") String author,
                             @JsonProperty("title") String title,
                             @JsonProperty("urlToImage") String urlToImage,
                             @JsonProperty("publishedAt") String publishedAt,
                             @JsonProperty("description") String description) {
                    this.author =author;
                    this.title = title;
                    this.urlToImage = urlToImage;
                    this.publishedAt = publishedAt;
                    this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
