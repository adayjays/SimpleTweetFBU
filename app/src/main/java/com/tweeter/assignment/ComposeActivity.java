package com.tweeter.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import okhttp3.Headers;
import com.tweeter.assignment.R;
import org.json.JSONException;
import org.parceler.Parcels;

import com.tweeter.assignment.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 280;
    private EditText editTextTweet;
    private TextView txtView;

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        editTextTweet = findViewById(R.id.etCompose);
        txtView = findViewById(R.id.txtView);
        findViewById(R.id.btnTweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextTweet.getText().toString().isEmpty()) {
                    return;
                }
                Toast.makeText(ComposeActivity.this, editTextTweet.getText().toString(), Toast.LENGTH_LONG).show();

                client.publishTweet(editTextTweet.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(ComposeActivity.this.getClass().getName(), "Successfully published tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(ComposeActivity.this.getClass().getName(), "Tweet is " + tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(ComposeActivity.this.getClass().getName(), "onFailure to publish tweet", throwable);
                    }
                });
            }
        });
        editTextTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtView.setText("Characters left: " + editTextTweet.getText().length() + "/280");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editTextTweet.getText().toString().length() > MAX_TWEET_LENGTH)
                    editTextTweet.setText(editTextTweet.getText().toString().substring(0, MAX_TWEET_LENGTH - 1));
            }
        });
    }
}