package com.example.android.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.newsapp.R;
import com.example.android.newsapp.news.RetrofitResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XXX on 05-Apr-18.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private static final String TAG = NewsAdapter.class.getSimpleName();
    private static ArrayList<RetrofitResult> list;

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView dateTextView;
        public TextView sectionTextView;
        public TextView authorTextView;

        public NewsViewHolder(View v) {
            super(v);
            authorTextView = v.findViewById(R.id.author_name);
            sectionTextView = v.findViewById(R.id.section_text);
            dateTextView = v.findViewById(R.id.date_text);
            titleTextView = v.findViewById(R.id.title_text);

            itemView.setOnClickListener(v1 -> {

                Context context = itemView.getContext();

                String url = list.get(getAdapterPosition()).getWebUrl();

                Uri newsUri = Uri.parse(url);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent, 0);

                if (activities.size() > 0) {

                    context.startActivity(webIntent);
                } else {

                    Toast.makeText(context, context.getResources().getString(R.string.no_web_browser_installed), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public NewsAdapter(ArrayList<RetrofitResult> news) {

        super();
        list = news;
    }

    @NonNull
    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsViewHolder holder, int position) {

        String date = list.get(position).getWebPublicationDate();
        String author = "";

        if (list.get(position).getTags() != null) {

            for (int i = 0; i < list.get(position).getTags().size(); i++) {

                String s = list.get(position).getTags().get(i).getWebTitle();

                author = author + s + "\n";
                holder.authorTextView.setText(author);
            }
        } else {

            holder.authorTextView.setVisibility(View.GONE);
        }

        holder.titleTextView.setText(list.get(position).getWebTitle());
        holder.sectionTextView.setText(list.get(position).getSectionName());
        holder.dateTextView.setText(setDate(date));
    }

    @Override
    public int getItemCount() {

        if (list != null) {

            return list.size();
        } else {

            return 0;
        }
    }

    private String setDate(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date newDate = dateFormat.parse(date);
            dateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy" + " , " + "HH:mm");
            date = dateFormat.format(newDate);

        } catch (ParseException e) {
            Log.e(TAG, "Error formatting the date", e);
        }

        return date;
    }
}
