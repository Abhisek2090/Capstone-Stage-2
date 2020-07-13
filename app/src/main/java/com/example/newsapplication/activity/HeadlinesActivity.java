package com.example.newsapplication.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.newsapplication.R;
import com.example.newsapplication.adapter.HeadlinesAdapter;

import com.example.newsapplication.application.NewsApplication;
import com.example.newsapplication.db.ArticleDatabase;
import com.example.newsapplication.model.ArticlesDataModel;

import com.example.newsapplication.model.ArticlesResponseModel;
import com.example.newsapplication.networking.Api;
import com.example.newsapplication.utils.ConnectivityReceiver;
import com.example.newsapplication.utils.Constants;
import com.example.newsapplication.utils.PaginationScrollListener;
import com.example.newsapplication.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.$Gson$Preconditions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HeadlinesActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private HeadlinesAdapter headlinesAdapter;
    private RecyclerView recyclerView;
    private TextView toolbar_title;
    private ArticleDatabase articleDatabase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private int currentPage = 1;
    private int pageSize = 10;

    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;

    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    private static final String TAG = HeadlinesActivity.class.getSimpleName();
    private ConnectivityReceiver connectivityReceiver;
    private  Snackbar snackbar;
    FrameLayout emptyView;
    private Button seeFavortiesButton;
    private String selectedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        toolbar_title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.headlines);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "RobotoSlab-Bold.ttf");
        toolbar_title.setTypeface(typeface);
        recyclerView = (RecyclerView) findViewById(R.id.headlinesRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        articleDatabase = ArticleDatabase.getInstance(this);

        headlinesAdapter = new HeadlinesAdapter();
        recyclerView.setAdapter(headlinesAdapter);
        emptyView = (FrameLayout)findViewById(R.id.emptyView);
        seeFavortiesButton = (Button)findViewById(R.id.seeFavortiesButton);

        seeFavortiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveTasks();
            }
        });

        getHeadlines();

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems(getString(R.string.headlines),getString(R.string.favourites));
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.equalsIgnoreCase(getString(R.string.headlines))) {
                    selectedItem = item;
                    swipeRefreshLayout.setEnabled(true);
                    currentPage = 1;
                    isLoading = false;
                    isLastPage = false;
                    getHeadlines();

                }
                else if(item.equalsIgnoreCase(getString(R.string.favourites))){
                    selectedItem = item;
                    retrieveTasks();
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                isLoading = false;
                isLastPage = false;
                getHeadlines();
            }
        });

        initSnackbar();
        initReceiver();
    }



    private void retrieveTasks() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getArticles().observe(this, new Observer<List<ArticlesDataModel>>() {
            @Override
            public void onChanged(List<ArticlesDataModel> articles) {
                Log.d(TAG, "updating list from viewmodel");
                isLastPage = true;
                isLoading = false;
                swipeRefreshLayout.setEnabled(false);
                headlinesAdapter.setHeadlines(articles);
            }
        });
    }

    private void initPaginationListener() {
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                currentPage++;
                isLoading = true;
               getMoreHeadlines();
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }
    private void startDetailsActivity(ArticlesDataModel articlesDataModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ARTCILE, articlesDataModel);
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(Constants.NEWS_DETAILS, bundle);
        startActivity(intent);
    }

    private void getHeadlines() {
        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the JacksonConverterFactory to directly convert json data to object
                .build();

        //creating the api interface
        Api api = retrofit.create(Api.class);


        Call<ArticlesResponseModel> call = api.getHeadLines("in", Constants.API_KEY ,currentPage, pageSize);

        call.enqueue(new Callback<ArticlesResponseModel>() {
            @Override
            public void onResponse(Call<ArticlesResponseModel> call, Response<ArticlesResponseModel> response) {
                if (response.body() != null) {
                    ArticlesResponseModel result = response.body();
                    headlinesAdapter.setHeadlines(result.getArticles());
                    initPaginationListener();
                    headlinesAdapter.setCallback(new HeadlinesAdapter.Callback() {
                    @Override
                    public void onListItemClick(View view, int position) {
                        startDetailsActivity(headlinesAdapter.getItemByPosition(position));
                    }
                });

                }
            }

            @Override
            public void onFailure(Call<ArticlesResponseModel> call, Throwable t) {
                Log.i(TAG, t.toString());
            }

        });

        swipeRefreshLayout.setRefreshing(false);


    }

    private void getMoreHeadlines() {
        //Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the JacksonConverterFactory to directly convert json data to object
                .build();

        //creating the api interface
        Api api = retrofit.create(Api.class);

        Call<ArticlesResponseModel> call = api.getHeadLines("in", Constants.API_KEY ,currentPage, pageSize);

        call.enqueue(new Callback<ArticlesResponseModel>() {
            @Override
            public void onResponse(Call<ArticlesResponseModel> call, Response<ArticlesResponseModel> response) {
                if (response.body() != null) {
                    ArticlesResponseModel result = response.body();
                    if(result.getArticles().isEmpty()) {
                        isLastPage = true;
                        headlinesAdapter.removeLoadingFooter();
                    }
                    else {
                        if(headlinesAdapter != null) {
                            headlinesAdapter.removeLoadingFooter();
                            headlinesAdapter.addAll(result.getArticles());
                            headlinesAdapter.addLoadingFooter();
                            isLoading = false;
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<ArticlesResponseModel> call, Throwable t) {
                Log.i(TAG, t.toString());
            }

        });


    }

    private void initReceiver() {
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver,intentFilter);
        NewsApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected) {
            if(snackbar != null) {
                snackbar.dismiss();
             //   emptyView.setVisibility(View.GONE);
                if( selectedItem != null && selectedItem.equalsIgnoreCase(getString(R.string.headlines)))
                getHeadlines();
            }
        }
        else {
            snackbar.show();
           // emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initSnackbar() {
        int color = Color.BLACK;
        snackbar = Snackbar
                .make(getWindow().getDecorView().getRootView(), R.string.newtwork_error, Snackbar.LENGTH_INDEFINITE);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(color);
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }
}
