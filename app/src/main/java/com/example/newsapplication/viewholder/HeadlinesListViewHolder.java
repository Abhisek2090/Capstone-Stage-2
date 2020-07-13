package com.example.newsapplication.viewholder;


import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.newsapplication.R;
import com.example.newsapplication.adapter.HeadlinesAdapter;
import com.example.newsapplication.model.ArticlesDataModel;


public class HeadlinesListViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView, sourceTextView, dateTextView;
    private ImageView imageView;
    private Context context;

    public HeadlinesListViewHolder(@NonNull View itemView, final HeadlinesAdapter.Callback callback) {
        super(itemView);
        this.context = itemView.getContext();
        initViews(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onListItemClick(v, getAdapterPosition());
                }
            }
        });

    }

    private void initViews(View itemView) {
        titleTextView= (TextView)itemView.findViewById(R.id.titleTextView);
        sourceTextView= (TextView)itemView.findViewById(R.id.sourceTextView);
        dateTextView= (TextView)itemView.findViewById(R.id.dateTextView);
        imageView = (ImageView) itemView.findViewById(R.id.headlineImageView);

        Typeface typeface_regular = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Regular.ttf");
        titleTextView.setTypeface(typeface_regular);
        Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Bold.ttf");
        sourceTextView.setTypeface(typeface_bold);
    }

    public void bindData(final ArticlesDataModel headlineItem) {
        String image_url = headlineItem.getUrlToImage();
        String title = headlineItem.getTitle();
        String date = headlineItem.getPublishedAt().substring(0,10);
        String source = headlineItem.getAuthor();

        titleTextView.setText(title);
        dateTextView.setText(date);
        sourceTextView.setText(source);

        Glide.with(context)
                .load(image_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .into(imageView);

    }
}
