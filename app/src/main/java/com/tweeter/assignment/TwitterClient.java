package com.tweeter.assignment;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

public class TwitterClient extends OAuthBaseClient {
	public static final String API_URL = "https://api.twitter.com/1.1";
	public static final String API_CONSUMER_KEY = "zy55FfCxu2Wrv2CmQvUlcZWc5";
	public static final String API_CONSUMER_SECRET = "R2bfzcWugSpSh31HrVV4lSOryz7CLsRHaWxXqbE8LVhu4hwX3q";
	public static final String API_CALLBACK_URL = "x-oauthflow-twitter://tweeter";
	public static final String STATUSES_HOME_TIMELINE_JSON = "statuses/home_timeline.json";
	public static final String STATUSES_UPDATE_JSON = "statuses/update.json";
	public static final BaseApi API_INSTANCE = TwitterApi.instance();

	public TwitterClient(Context context) {
		super(context, API_INSTANCE,
				API_URL,
				API_CONSUMER_KEY,
				API_CONSUMER_SECRET,
				null,
				API_CALLBACK_URL);
	}

	public void publishTweet(String tweetContent, JsonHttpResponseHandler jsonHttpResponseHandler) {
		String tweeterApiUrl = getApiUrl(STATUSES_UPDATE_JSON);
		RequestParams requestParams = new RequestParams();
		requestParams.put("status", tweetContent);
		client.post(tweeterApiUrl, requestParams, "", jsonHttpResponseHandler);
	}
	public void getHomeTimeline(JsonHttpResponseHandler jsonHttpResponseHandler) {
		String tweeterApiUrl = getApiUrl(STATUSES_HOME_TIMELINE_JSON);
		RequestParams requestParams = new RequestParams();
		requestParams.put("since_id", 1);
		requestParams.put("count", 25);
		client.get(tweeterApiUrl, requestParams, jsonHttpResponseHandler);
	}

	public void getNextPageOfTweets(JsonHttpResponseHandler jsonHttpResponseHandler, long maximumId) {
		String tweeterApiUrl = getApiUrl(STATUSES_HOME_TIMELINE_JSON);
		RequestParams requestParams = new RequestParams();
		requestParams.put("max_id", maximumId);
		requestParams.put("count", 25);
		client.get(tweeterApiUrl, requestParams, jsonHttpResponseHandler);
	}
}
