package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by XXX on 05-Apr-18.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> news) {

        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listView = convertView;

        if (listView == null) {

            listView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list, parent, false);
        }

        News currentNews = getItem(position);

        TextView nameText = listView.findViewById(R.id.author_name);
        nameText.setText(currentNews.getName());

        RatingBar ratingBar = listView.findViewById(R.id.rating_bar);

        if (currentNews.getRating() != 0) {

            ratingBar.setNumStars(currentNews.getRating());
        } else {

            ratingBar.setVisibility(View.GONE);
        }

        TextView titleText = listView.findViewById(R.id.title_text);
        titleText.setText(currentNews.getTitle());

        TextView sectionText = listView.findViewById(R.id.section_text);
        sectionText.setText(currentNews.getSection());

        TextView dateText = listView.findViewById(R.id.date_text);
        dateText.setText(currentNews.getDate());

        return listView;
    }

}
