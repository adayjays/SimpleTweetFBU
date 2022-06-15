package com.tweeter.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tweeter.assignment.R;
import com.tweeter.assignment.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private List<Tweet> tweetList;
    private Context context;

    public TweetAdapter(Context context, List<Tweet> tweetList) {
        this.context = context;
        this.tweetList = tweetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tweet, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    public void clear() {
        tweetList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList) {
        this.tweetList.addAll(tweetList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewUsername;
        TextView textViewBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewProfileImage);
            textViewBody = itemView.findViewById(R.id.textViewTweetDescription);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
        }

        public void bind(Tweet tweet) {
            textViewUsername.setText(tweet.user.screenName);
            textViewBody.setText(tweet.body);
            Glide.with(context).load(tweet.user.profileImageUrl).into(imageView);
        }
    }
}

