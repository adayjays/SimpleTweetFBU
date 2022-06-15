package com.tweeter.assignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.tweeter.assignment.R;
import com.tweeter.assignment.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import okhttp3.Headers;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewTweets;
    private TwitterClient twitterClient;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList;
    private ProgressBar progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        progressDialog = new ProgressBar(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setVisibility(View.GONE);
        twitterClient = TwitterApp.getRestClient(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomeTimeline();
            }
        });
        recyclerViewTweets = findViewById(R.id.recyclerViewTweets);
        tweetList = new ArrayList<>();
        tweetAdapter = new TweetAdapter(this, tweetList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewTweets.setLayoutManager(linearLayoutManager);
        recyclerViewTweets.setAdapter(tweetAdapter);
        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreTweets();
            }
        };
        recyclerViewTweets.addOnScrollListener(endlessRecyclerViewScrollListener);
        loadHomeTimeline();
    }

    private void loadMoreTweets() {
        twitterClient.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweetList = Tweet.fromJsonArray(jsonArray);
                    tweetAdapter.addAll(tweetList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TimelineActivity.class.getName(), "Failed to load more data...", throwable);
            }
        }, tweetList.get(tweetList.size() - 1).id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweetList.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            recyclerViewTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadHomeTimeline() {
        progressDialog.setVisibility(View.VISIBLE);
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TimelineActivity.class.getName(), "Successfully fetched Home Timeline" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweetAdapter.clear();
                    tweetAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TimelineActivity.class.getName(), "Failed to fetch Home Timeline", e);
                     e.printStackTrace();
                }
                progressDialog.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TimelineActivity.class.getName(), "Failed to fetch Home Timeline!", throwable);
                progressDialog.setVisibility(View.GONE);
            }
        });
    }
}