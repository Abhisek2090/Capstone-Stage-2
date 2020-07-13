package com.example.newsapplication;

import com.example.newsapplication.adapter.HeadlinesAdapter;
import com.example.newsapplication.model.ArticlesDataModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)

public class HeadlinesAdapterTest {
    private HeadlinesAdapter adapter;

    ArticlesDataModel aricle1 = new ArticlesDataModel("A", "a1", ".html", "21.11","article1" );
    ArticlesDataModel article2 = new ArticlesDataModel("B", "b1", ".html", "21.12","article2" );



    @Before
    public void setUp() {
        List<ArticlesDataModel> articles = new ArrayList<>();
        articles.add(aricle1);
        articles.add(article2);

      //  adapter = new HeadlinesAdapter(articles);
    }

    @Test
    public void itemCount() {
        assertThat(adapter.getItemCount()).isEqualTo(2);
    }

    @Test
    public void getItemAtPosition() {
        assertThat(adapter.getItemByPosition(0)).isEqualTo(aricle1);
        assertThat(adapter.getItemByPosition(1)).isEqualTo(article2);
    }
}
