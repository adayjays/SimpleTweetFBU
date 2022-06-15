package com.tweeter.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tweeter.assignment.R;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewById(R.id.buttonLogin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getClient().connect();
					}
				}
		);
	}
	@Override
	public void onLoginSuccess() {
		Log.i(this.getClass().getName(), "Login successful");
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
	}

	@Override
	public void onLoginFailure(Exception e) {
		Log.i(this.getClass().getName(), "Login failed");
		e.printStackTrace();
	}

}
