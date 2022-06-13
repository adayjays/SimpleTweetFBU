# RestClientTemplate [![Build Status](https://travis-ci.org/codepath/android-rest-client-template.svg?branch=master)](https://travis-ci.org/codepath/android-rest-client-template)

## Overview

RestClientTemplate is a skeleton Android project that makes writing Android apps sourced from OAuth JSON REST APIs as easy as possible. This skeleton project
combines the best libraries and structure to enable quick development of rich API clients. The following things are supported out of the box:

 * Authenticating with any OAuth 1.0a or OAuth 2 API
 * Sending requests for and parsing JSON API data using a defined client
 * Persisting data to a local SQLite store through an ORM layer
 * Displaying and caching remote image data into views

The following libraries are used to make this possible:

 * [scribe-java](https://github.com/fernandezpablo85/scribe-java) - Simple OAuth library for handling the authentication flow.
 * [Android Async HTTP](https://github.com/codepath/AsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler) - Custom-built library for managing OAuth authentication and signing of requests
 * [Glide](https://github.com/bumptech/glide) - Used for async image loading and caching them in memory and on disk.
 * [Room](https://developer.android.com/training/data-storage/room/index.html) - Simple ORM for persisting a local SQLite database on the Android device

## Usage

### 1. Configure the REST client

Open `src/com.codepath.apps.restclienttemplate/RestClient.java`. Configure the `REST_API_INSTANCE` and`REST_URL`.
 
For example if I wanted to connect to Twitter:

```java
// RestClient.java
public class RestClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
    public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties
    // ...constructor and endpoints
}
```

Rename the `apikey.properties.example` file to `apikey.properties`.   Replace the `CONSUMER_KEY` and `CONSUMER_SECRET` to the values specified in the Twitter console:

CONSUMER_KEY="adsflfajsdlfdsajlafdsjl"
CONSUMER_SECRET="afdsljkasdflkjsd"

Next, change the `intent_scheme` and `intent_host` in `strings.xml` to a unique name that is special for this application.
This is used for the OAuth authentication flow for launching the app through web pages through an [Android intent](https://developer.chrome.com/multidevice/android/intents).

[comment]: <> (```xml)

[comment]: <> (<string name="intent_scheme">oauth</string>)

[comment]: <> (<string name="intent_host">codepathtweets</string>)

[comment]: <> (```)

[comment]: <> (Next, you want to define the endpoints which you want to retrieve data from or send data to within your client:)

[comment]: <> (```java)

[comment]: <> (// RestClient.java)

[comment]: <> (public void getHomeTimeline&#40;int page, JsonHttpResponseHandler handler&#41; {)

[comment]: <> (  String apiUrl = getApiUrl&#40;"statuses/home_timeline.json"&#41;;)

[comment]: <> (  RequestParams params = new RequestParams&#40;&#41;;)

[comment]: <> (  params.put&#40;"page", String.valueOf&#40;page&#41;&#41;;)

[comment]: <> (  getClient&#40;&#41;.get&#40;apiUrl, params, handler&#41;;)

[comment]: <> (})

[comment]: <> (```)

[comment]: <> (Note we are using `getApiUrl` to get the full URL from the relative fragment and `RequestParams` to control the request parameters.)

[comment]: <> (You can easily send post requests &#40;or put or delete&#41; using a similar approach:)

[comment]: <> (```java)

[comment]: <> (// RestClient.java)

[comment]: <> (public void postTweet&#40;String body, JsonHttpResponseHandler handler&#41; {)

[comment]: <> (    String apiUrl = getApiUrl&#40;"statuses/update.json"&#41;;)

[comment]: <> (    RequestParams params = new RequestParams&#40;&#41;;)

[comment]: <> (    params.put&#40;"status", body&#41;;)

[comment]: <> (    getClient&#40;&#41;.post&#40;apiUrl, params, handler&#41;;)

[comment]: <> (})

[comment]: <> (```)

[comment]: <> (These endpoint methods will automatically execute asynchronous requests signed with the authenticated access token. To use JSON endpoints, simply invoke the method)

[comment]: <> (with a `JsonHttpResponseHandler` handler:)

[comment]: <> (```java)

[comment]: <> (// SomeActivity.java)

[comment]: <> (RestClient client = RestApplication.getRestClient&#40;&#41;;)

[comment]: <> (client.getHomeTimeline&#40;1, new JsonHttpResponseHandler&#40;&#41; {)

[comment]: <> (    @Override)

[comment]: <> (    public void onSuccess&#40;int statusCode, Headers headers, JSON json&#41; {)

[comment]: <> (    // json.jsonArray.getJSONObject&#40;0&#41;.getLong&#40;"id"&#41;;)

[comment]: <> (  })

[comment]: <> (}&#41;;)

[comment]: <> (```)

[comment]: <> (Based on the JSON response &#40;array or object&#41;, you need to declare the expected type inside the OnSuccess signature i.e)

[comment]: <> (`public void onSuccess&#40;JSONObject json&#41;`. If the endpoint does not return JSON, then you can use the `AsyncHttpResponseHandler`:)

[comment]: <> (```java)

[comment]: <> (RestClient client = RestApplication.getRestClient&#40;&#41;;)

[comment]: <> (client.getSomething&#40;new JsonHttpResponseHandler&#40;&#41; {)

[comment]: <> (    @Override)

[comment]: <> (    public void onSuccess&#40;int statusCode, Headers headers, String response&#41; {)

[comment]: <> (        System.out.println&#40;response&#41;;)

[comment]: <> (    })

[comment]: <> (}&#41;;)

[comment]: <> (```)

[comment]: <> (Check out [Android Async HTTP Docs]&#40;https://github.com/codepath/AsyncHttpClient&#41; for more request creation details.)

[comment]: <> (### 2. Define the Models)

[comment]: <> (In the `src/com.codepath.apps.restclienttemplate.models`, create the models that represent the key data to be parsed and persisted within your application.)

[comment]: <> (For example, if you were connecting to Twitter, you would want a Tweet model as follows:)

[comment]: <> (```java)

[comment]: <> (// models/Tweet.java)

[comment]: <> (package com.codepath.apps.restclienttemplate.models;)

[comment]: <> (import androidx.room.ColumnInfo;)

[comment]: <> (import androidx.room.Embedded;)

[comment]: <> (import androidx.room.Entity;)

[comment]: <> (import androidx.room.PrimaryKey;)

[comment]: <> (import org.json.JSONException;)

[comment]: <> (import org.json.JSONObject;)

[comment]: <> (@Entity)

[comment]: <> (public class Tweet {)

[comment]: <> (  // Define database columns and associated fields)

[comment]: <> (  @PrimaryKey)

[comment]: <> (  @ColumnInfo)

[comment]: <> (  Long id;)

[comment]: <> (  @ColumnInfo)

[comment]: <> (  String userHandle;)

[comment]: <> (  @ColumnInfo)

[comment]: <> (  String timestamp;)

[comment]: <> (  @ColumnInfo)

[comment]: <> (  String body;)

[comment]: <> (  // Use @Embedded to keep the column entries as part of the same table while still)

[comment]: <> (  // keeping the logical separation between the two objects.)

[comment]: <> (  @Embedded)

[comment]: <> (  User user;)

[comment]: <> (})

[comment]: <> (```)

[comment]: <> (Note there is a separate `User` object but it will not actually be declared as a separate table.  By using the `@Embedded` annotation, the fields in this class will be stored as part of the Tweet table.  Room specifically does not load references between two different entities for performance reasons &#40;see https://developer.android.com/training/data-storage/room/referencing-data&#41;, so declaring it this way causes the data to be denormalized as one table.)

[comment]: <> (```java)

[comment]: <> (// models/User.java)

[comment]: <> (public class User {)

[comment]: <> (    @ColumnInfo)

[comment]: <> (    String name;)

[comment]: <> (    // normally this field would be annotated @PrimaryKey because this is an embedded object)

[comment]: <> (    // it is not needed)

[comment]: <> (    @ColumnInfo  )

[comment]: <> (    Long twitter_id;)

[comment]: <> (})

[comment]: <> (```)

[comment]: <> (Notice here we specify the SQLite table for a resource, the columns for that table, and a constructor for turning the JSON object fetched from the API into this object. For more information on creating a model, check out the [Room guide]&#40;https://developer.android.com/training/data-storage/room/&#41;.)

[comment]: <> (In addition, we also add functions into the model to support parsing JSON attributes in order to instantiate the model based on API data.  For the User object, the parsing logic would be:)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;// Parse model from JSON&#41;)

[comment]: <> ([comment]: <> &#40;public static User parseJSON&#40;JSONObject tweetJson&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;    User user = new User&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    this.twitter_id = tweetJson.getLong&#40;"id"&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    this.name = tweetJson.getString&#40;"name"&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    return user;&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;For the Tweet object, the logic would would be:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;// models/Tweet.java&#41;)

[comment]: <> ([comment]: <> &#40;@Entity&#41;)

[comment]: <> ([comment]: <> &#40;public class Tweet {&#41;)

[comment]: <> ([comment]: <> &#40;  // ...existing code from above...&#41;)

[comment]: <> ([comment]: <> &#40;  // Add a constructor that creates an object from the JSON response&#41;)

[comment]: <> ([comment]: <> &#40;  public Tweet&#40;JSONObject object&#41;{&#41;)

[comment]: <> ([comment]: <> &#40;    try {&#41;)

[comment]: <> ([comment]: <> &#40;      this.user = User.parseJSON&#40;object.getJSONObject&#40;"user"&#41;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;      this.userHandle = object.getString&#40;"user_username"&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;      this.timestamp = object.getString&#40;"timestamp"&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;      this.body = object.getString&#40;"body"&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    } catch &#40;JSONException e&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;      e.printStackTrace&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    }&#41;)

[comment]: <> ([comment]: <> &#40;  }&#41;)

[comment]: <> ([comment]: <> &#40;  public static ArrayList<Tweet> fromJson&#40;JSONArray jsonArray&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;    ArrayList<Tweet> tweets = new ArrayList<Tweet>&#40;jsonArray.length&#40;&#41;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    for &#40;int i=0; i < jsonArray.length&#40;&#41;; i++&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;        JSONObject tweetJson = null;&#41;)

[comment]: <> ([comment]: <> &#40;        try {&#41;)

[comment]: <> ([comment]: <> &#40;            tweetJson = jsonArray.getJSONObject&#40;i&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;        } catch &#40;Exception e&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;            e.printStackTrace&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;            continue;&#41;)

[comment]: <> ([comment]: <> &#40;        }&#41;)

[comment]: <> ([comment]: <> &#40;        Tweet tweet = new Tweet&#40;tweetJson&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;        tweets.add&#40;tweet&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    }&#41;)

[comment]: <> ([comment]: <> &#40;    return tweets;&#41;)

[comment]: <> ([comment]: <> &#40;  }&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)


[comment]: <> ([comment]: <> &#40;Now you have a model that supports proper creation based on JSON. Create models for all the resources necessary for your mobile client.&#41;)

[comment]: <> ([comment]: <> &#40;### 4. Define your queries&#41;)

[comment]: <> ([comment]: <> &#40;Next, you will need to define the queries by creating a Data Access Object &#40;DAO&#41; class.   Here is an example of declaring queries to return a Tweet by the post ID, retrieve the most recent tweets, and insert tweets.   &#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;import androidx.room.Dao;&#41;)

[comment]: <> ([comment]: <> &#40;import androidx.room.Insert;&#41;)

[comment]: <> ([comment]: <> &#40;import androidx.room.OnConflictStrategy;&#41;)

[comment]: <> ([comment]: <> &#40;import androidx.room.Query;&#41;)

[comment]: <> ([comment]: <> &#40;import java.util.List;&#41;)

[comment]: <> ([comment]: <> &#40;@Dao&#41;)

[comment]: <> ([comment]: <> &#40;public interface TwitterDao {&#41;)

[comment]: <> ([comment]: <> &#40;    // Record finders&#41;)

[comment]: <> ([comment]: <> &#40;    @Query&#40;"SELECT * FROM Tweet WHERE post_id = :tweetId"&#41;&#41;)

[comment]: <> ([comment]: <> &#40;    Tweet byTweetId&#40;Long tweetId&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    @Query&#40;"SELECT * FROM Tweet ORDER BY created_at"&#41;&#41;)

[comment]: <> ([comment]: <> &#40;    List<Tweet> getRecentTweets&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    // Replace strategy is needed to ensure an update on the table row.  Otherwise the insertion will&#41;)

[comment]: <> ([comment]: <> &#40;    // fail.&#41;)

[comment]: <> ([comment]: <> &#40;    @Insert&#40;onConflict = OnConflictStrategy.REPLACE&#41;&#41;)

[comment]: <> ([comment]: <> &#40;    void insertTweet&#40;Tweet... tweets&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;The examples here show how to perform basic queries on the Tweet table.  If you need to declare one-to-many or many-to-many relations, see the guides on using the [@Relation]&#40;https://developer.android.com/reference/android/arch/persistence/room/Relation&#41; and [@ForeignKey]&#40;https://developer.android.com/reference/android/arch/persistence/room/ForeignKey&#41; annotations.&#41;)

[comment]: <> ([comment]: <> &#40;### 5. Create database&#41;)

[comment]: <> ([comment]: <> &#40;We need to define a database that extends `RoomDatabase` and describe which entities as part of this database. We also need to include what data access objects are to be included.  If the entities are modified or additional ones are included, the version number will need to be changed.  Note that only the `Tweet` class is declared:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;// bump version number if your schema changes&#41;)

[comment]: <> ([comment]: <> &#40;@Database&#40;entities={Tweet.class}, version=1&#41;&#41;)

[comment]: <> ([comment]: <> &#40;public abstract class MyDatabase extends RoomDatabase {&#41;)

[comment]: <> ([comment]: <> &#40;  // Declare your data access objects as abstract&#41;)

[comment]: <> ([comment]: <> &#40;  public abstract TwitterDao twitterDao&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;  // Database name to be used&#41;)

[comment]: <> ([comment]: <> &#40;  public static final String NAME = "MyDataBase";&#41;)

[comment]: <> ([comment]: <> &#40;```    &#41;)

[comment]: <> ([comment]: <> &#40;When compiling the code, the schemas will be stored in a `schemas/` directory assuming this statement&#41;)

[comment]: <> ([comment]: <> &#40;has been included your `app/build.gradle` file.  These schemas should be checked into your code based.&#41;)

[comment]: <> ([comment]: <> &#40;```gradle&#41;)

[comment]: <> ([comment]: <> &#40;android {&#41;)

[comment]: <> ([comment]: <> &#40;    defaultConfig {&#41;)

[comment]: <> ([comment]: <> &#40;        javaCompileOptions {&#41;)

[comment]: <> ([comment]: <> &#40;            annotationProcessorOptions {&#41;)

[comment]: <> ([comment]: <> &#40;                arguments = ["room.schemaLocation": "$projectDir/schemas".toString&#40;&#41;]&#41;)

[comment]: <> ([comment]: <> &#40;            }&#41;)

[comment]: <> ([comment]: <> &#40;        }&#41;)

[comment]: <> ([comment]: <> &#40;    }&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;### 6. Initialize database&#41;)

[comment]: <> ([comment]: <> &#40;Inside your application class, you will need to initialize the database and specify a name for it.&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;public class RestClientApp extends Application {&#41;)

[comment]: <> ([comment]: <> &#40;  MyDatabase myDatabase;&#41;)

[comment]: <> ([comment]: <> &#40;  @Override&#41;)

[comment]: <> ([comment]: <> &#40;  public void onCreate&#40;&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;    // when upgrading versions, kill the original tables by using fallbackToDestructiveMigration&#40;&#41;&#41;)

[comment]: <> ([comment]: <> &#40;    myDatabase = Room.databaseBuilder&#40;this, MyDatabase.class, MyDatabase.NAME&#41;.fallbackToDestructiveMigration&#40;&#41;.build&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;  }&#41;)

[comment]: <> ([comment]: <> &#40;  public MyDatabase getMyDatabase&#40;&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;    return myDatabase;&#41;)

[comment]: <> ([comment]: <> &#40;  }&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;### 7. Setup Your Authenticated Activities&#41;)

[comment]: <> ([comment]: <> &#40;Open `src/com.codepath.apps.restclienttemplate/LoginActivity.java` and configure the `onLoginSuccess` method&#41;)

[comment]: <> ([comment]: <> &#40;which fires once your app has access to the authenticated API. Launch an activity and begin using your REST client:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;// LoginActivity.java&#41;)

[comment]: <> ([comment]: <> &#40;@Override&#41;)

[comment]: <> ([comment]: <> &#40;public void onLoginSuccess&#40;&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;  Intent i = new Intent&#40;this, TimelineActivity.class&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;  startActivity&#40;i&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;In your new authenticated activity, you can access your client anywhere with:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;RestClient client = RestApplication.getRestClient&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;client.getHomeTimeline&#40;1, new JsonHttpResponseHandler&#40;&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;  public void onSuccess&#40;int statusCode, Headers headers, JSON json&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;    Log.d&#40;"DEBUG", "timeline: " + json.jsonArray.toString&#40;&#41;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;    // Load json array into model classes&#41;)

[comment]: <> ([comment]: <> &#40;  }&#41;)

[comment]: <> ([comment]: <> &#40;}&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;You can then load the data into your models from a `JSONArray` using:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;ArrayList<Tweet> tweets = Tweet.fromJSON&#40;jsonArray&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;or load the data from a single `JSONObject` with:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;Tweet t = new Tweet&#40;json&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;// t.body = "foo"&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;To save, you will need to perform the database operation on a separate thread by creating an `AsyncTask` and adding the item:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;AsyncTask<Tweet, Void, Void> task = new AsyncTask<Tweet, Void, Void>&#40;&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;    @Override&#41;)

[comment]: <> ([comment]: <> &#40;    protected Void doInBackground&#40;Tweet... tweets&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;      TwitterDao twitterDao = &#40;&#40;RestApplication&#41; getApplicationContext&#40;&#41;&#41;.getMyDatabase&#40;&#41;.twitterDao&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;      twitterDao.insertModel&#40;tweets&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;      return null;&#41;)

[comment]: <> ([comment]: <> &#40;    };&#41;)

[comment]: <> ([comment]: <> &#40;  };&#41;)

[comment]: <> ([comment]: <> &#40;  task.execute&#40;tweets&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;That's all you need to get started. From here, hook up your activities and their behavior, adjust your models and add more REST endpoints.&#41;)

[comment]: <> ([comment]: <> &#40;### Extras&#41;)

[comment]: <> ([comment]: <> &#40;#### Loading Images with Glide&#41;)

[comment]: <> ([comment]: <> &#40;If you want to load a remote image url into a particular ImageView, you can use Glide to do that with:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;Glide.with&#40;this&#41;.load&#40;imageUrl&#41;&#41;)

[comment]: <> ([comment]: <> &#40;     .into&#40;imageView&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;This will load an image into the specified ImageView and resize the image to fit.&#41;)

[comment]: <> ([comment]: <> &#40;#### Logging Out&#41;)

[comment]: <> ([comment]: <> &#40;You can log out by clearing the access token at any time through the client object:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;RestClient client = RestApplication.getRestClient&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;client.clearAccessToken&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;### Viewing SQL table&#41;)

[comment]: <> ([comment]: <> &#40;You can use `chrome://inspect` to view the SQL tables once the app is running on your emulator.  See [this guide]&#40;https://guides.codepath.com/android/Debugging-with-Stetho&#41; for more details.&#41;)

[comment]: <> ([comment]: <> &#40;### Adding OAuth2 support&#41;)

[comment]: <> ([comment]: <> &#40;Google uses OAuth2 APIs so make sure to use the `GoogleApi20` instance:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;public static final BaseApi REST_API_INSTANCE = GoogleApi20.instance&#40;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;Change `REST_URL` to use the Google API:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;public static final String REST_URL = "https://www.googleapis.com/calendar/v3"; // Change this, base API URL&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;The consumer and secret keys should be retrieved via [the credentials section]&#40;https://console.developers.google.com/apis/credentials&#41; in the Google developer console  You will need to create an OAuth2 client ID and client secret.&#41;)

[comment]: <> ([comment]: <> &#40;Create a file called `apikey.properties`: &#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;REST_CONSUMER_KEY="XXX-XXX.apps.googleusercontent.com"&#41;)

[comment]: <> ([comment]: <> &#40;REST_CONSUMER_SECRET="XX-XXXXXXX"&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;The OAuth2 scopes should be used according to the ones defined in [the OAuth2 scopes]&#40;https://developers.google.com/identity/protocols/googlescopes&#41;:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;public static final String OAUTH2_SCOPE = "https://www.googleapis.com/auth/calendar.readonly";&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;Make sure to pass this value into the scope parameter:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;public RestClient&#40;Context context&#41; {&#41;)

[comment]: <> ([comment]: <> &#40;		super&#40;context, REST_API_INSTANCE,&#41;)

[comment]: <> ([comment]: <> &#40;				REST_URL,&#41;)

[comment]: <> ([comment]: <> &#40;				REST_CONSUMER_KEY,&#41;)

[comment]: <> ([comment]: <> &#40;				REST_CONSUMER_SECRET,&#41;)

[comment]: <> ([comment]: <> &#40;				OAUTH2_SCOPE,  // OAuth2 scope, null for OAuth1&#41;)

[comment]: <> ([comment]: <> &#40;				String.format&#40;REST_CALLBACK_URL_TEMPLATE, context.getString&#40;R.string.intent_host&#41;,&#41;)

[comment]: <> ([comment]: <> &#40;						context.getString&#40;R.string.intent_scheme&#41;, context.getPackageName&#40;&#41;, FALLBACK_URL&#41;&#41;;&#41;)

[comment]: <> ([comment]: <> &#40;	}&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;Google only accepts `http://` or `https://` domains, so your `REST_CALLBACK_URL_TEMPLATE` will need to be adjusted:&#41;)

[comment]: <> ([comment]: <> &#40;```java&#41;)

[comment]: <> ([comment]: <> &#40;public static final String REST_CALLBACK_URL_TEMPLATE = "https://localhost";&#41;)

[comment]: <> ([comment]: <> &#40;```&#41;)

[comment]: <> ([comment]: <> &#40;Make sure to update the `cprest` and `intent_host` to match this callback URL . &#41;)

[comment]: <> ([comment]: <> &#40;### Troubleshooting&#41;)

[comment]: <> ([comment]: <> &#40;* If you receive the following error `org.scribe.exceptions.OAuthException: Cannot send unauthenticated requests for TwitterApi client. Please attach an access token!` then check the following:&#41;)

[comment]: <> ([comment]: <> &#40; * Is your intent-filter with `<data>` attached to the `LoginActivity`? If not, make sure that the `LoginActivity` receives the request after OAuth authorization.&#41;)

[comment]: <> ([comment]: <> &#40; * Is the `onLoginSuccess` method being executed in the `LoginActivity`. On launch of your app, be sure to start the app on the LoginActivity so authentication routines execute on launch and take you to the authenticated activity.&#41;)

[comment]: <> ([comment]: <> &#40; * If you are plan to test with Android API 24 or above, you will need to use Chrome to launch the OAuth flow.  &#41;)

[comment]: <> ([comment]: <> &#40; * Note that the emulators &#40;both the Google-provided x86 and Genymotion versions&#41; for API 24+ versions can introduce intermittent issues when initiating the OAuth flow for the first time.  For best results, use an device for this project.&#41;)
