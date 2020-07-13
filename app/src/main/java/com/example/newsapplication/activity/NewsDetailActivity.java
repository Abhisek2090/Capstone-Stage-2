package com.example.newsapplication.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newsapplication.R;
import com.example.newsapplication.db.ArticleDatabase;
import com.example.newsapplication.model.ArticlesDataModel;
import com.example.newsapplication.utils.AppExecutors;
import com.example.newsapplication.utils.Constants;

public class NewsDetailActivity extends AppCompatActivity {
    private TextView titleTextView, sourceTextView, dateTextView, descTextView;
    private ImageView imageView;
    private Button favButton;
    private ArticleDatabase articleDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

        Bundle bundle = getIntent().getBundleExtra(Constants.NEWS_DETAILS);
        ArticlesDataModel article = (ArticlesDataModel) bundle.getSerializable(Constants.ARTCILE);
        initViews();
        populateNewsDetails(article);
        articleDatabase = ArticleDatabase.getInstance(getApplicationContext());


    }

    private void initViews() {
        titleTextView= (TextView)findViewById(R.id.titleTextView);
        sourceTextView= (TextView)findViewById(R.id.sourceTextView);
        dateTextView= (TextView)findViewById(R.id.dateTextView);
        descTextView= (TextView)findViewById(R.id.descTextView);
        imageView = (ImageView)findViewById(R.id.headlineImageView);
        favButton = findViewById(R.id.addToFavButton);


        Typeface typeface_regular = Typeface.createFromAsset(getAssets(), "RobotoSlab-Regular.ttf");
        sourceTextView.setTypeface(typeface_regular);
        descTextView.setTypeface(typeface_regular);
        Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "RobotoSlab-Bold.ttf");
        titleTextView.setTypeface(typeface_bold);
    }

    private void populateNewsDetails(final ArticlesDataModel article) {

        String image_url = article.getUrlToImage();
        String title = article.getTitle();
        String date = article.getPublishedAt().substring(0,10);
        String source = article.getAuthor();
        String desc = article.getDescription();

        titleTextView.setText(title);
        dateTextView.setText(date);
        sourceTextView.setText(source);
        descTextView.setText(desc);

        Glide.with(this)
                .load(image_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .into(imageView);


        if(article.isFav()) {
            favButton.setText(R.string.favourite);
            favButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!article.isFav()) {

                    AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            article.setFav(true);
                           articleDatabase.articleDao().insertArticle(article);
                        }
                    });

                    favButton.setText(R.string.favourite);
                    favButton.setBackground(getResources().getDrawable(R.drawable.button_backgrond));
                }
                else {
                    AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            article.setFav(false);
                            articleDatabase.articleDao().removeFromFav(article);
                        }
                    });

                    favButton.setText(R.string.add_to_favourites);
                    favButton.setBackground(getResources().getDrawable(R.drawable.button_backgrond_gray));

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }




}
