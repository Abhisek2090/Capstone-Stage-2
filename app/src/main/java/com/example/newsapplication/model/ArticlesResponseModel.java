package com.example.newsapplication.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticlesResponseModel implements Serializable {

        private List<ArticlesDataModel> articles;

        public ArticlesResponseModel(@JsonProperty("articles") List<ArticlesDataModel> articles ) {
            this.articles = articles;
        }
        public List<ArticlesDataModel> getArticles() {
                return articles;
        }
}
