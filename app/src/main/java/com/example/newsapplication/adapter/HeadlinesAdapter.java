package com.example.newsapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapplication.R;
import com.example.newsapplication.model.ArticlesDataModel;
import com.example.newsapplication.utils.Constants;
import com.example.newsapplication.viewholder.HeadlinesListViewHolder;

import java.util.ArrayList;
import java.util.List;


public class HeadlinesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final String TAG = HeadlinesAdapter.class.getSimpleName();
    private List<ArticlesDataModel> headlines = new ArrayList<>();
    private Callback callback;
    private boolean isLoadingAdded = false;
    RecyclerView.ViewHolder viewHolder = null;

    public HeadlinesAdapter() {}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

         switch (viewType) {
            case Constants.ITEM:
                view = inflater.inflate(R.layout.list_headline_item, parent, false);
                viewHolder = new HeadlinesListViewHolder(view, callback);
                break;


             case Constants.LOADING:
                 View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                 viewHolder = new LoadingVH(v2);
                 break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (getItemViewType(position)) {
            case Constants.ITEM:
                ((HeadlinesListViewHolder)viewHolder).bindData(getItemByPosition(position));
                break;
            case Constants.LOADING:
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position == headlines.size()  && isLoadingAdded) ? Constants.LOADING:Constants.ITEM;
    }

    @Override
    public int getItemCount() {
        return isLoadingAdded? headlines.size()+1: headlines.size();
    }

    public void addAll(List<ArticlesDataModel> list) {
        headlines.addAll(list);
        notifyItemInserted(headlines.size());
    }

    static class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    public ArticlesDataModel getItemByPosition(int position) {
     return headlines.get(position);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {

    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = headlines.size() - 1;
        ArticlesDataModel result = getItem(position);

        if (result != null) {
            notifyItemRemoved(position+1);
        }
    }

    public void setHeadlines(List<ArticlesDataModel> headlines) {
        this.headlines = headlines;
        notifyDataSetChanged();
    }

    public ArticlesDataModel getItem(int position) {
        return headlines.get(position);
    }

    public interface Callback {
        void onListItemClick(View view, int position);
    }
}
