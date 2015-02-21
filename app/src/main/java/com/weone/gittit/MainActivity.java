package com.weone.gittit;

import android.app.Notification;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends ActionBarActivity {


    protected static final String TAG = MainActivity.class.getSimpleName();
    protected int NUMBER_OF_POSTS = 20;
    protected ListView mBlogList;
    protected JSONObject mBlogData;
    protected String[] mBlogPostTitles;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mEmptyTextView = (TextView)findViewById(android.R.id.empty);
        mBlogList = (ListView)findViewById(R.id.blog_list);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(isNetworkAvailable()) {
            //Crouton.makeText(MainActivity.this, "Network is Available", Style.INFO);
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        Boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
           // Crouton.makeText(MainActivity.this, "Network is Available", Style.INFO);
        }
        else {
            Toast.makeText(MainActivity.this, "Network Error",Toast.LENGTH_LONG).show();
            //Crouton.makeText(MainActivity.this, "Network not Available", Style.ALERT);
        }

        return isAvailable;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void updateList() {
        if(mBlogData == null) {
            //TODO handle error
        }
        else {
            try {
                JSONArray jsonPosts = mBlogData.getJSONArray("posts");
                mBlogPostTitles = new String[jsonPosts.length()];

                for(int i=0; i< jsonPosts.length(); i++) {
                    JSONObject jsonPost = jsonPosts.getJSONObject(i);
                    String title = jsonPost.getString("title");
                    title = Html.fromHtml(title).toString();
                    mBlogPostTitles[i] = title;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        mBlogPostTitles
                        );
                mBlogList.setAdapter(adapter);

                Log.i(TAG,mBlogData.toString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class GetBlogPostsTask extends AsyncTask<Object ,Void, JSONObject> {
        //param1 is the param to be passed to doInBack method
        //2nd is for progress
        //3rd is the type of object to be returned
        @Override
        protected JSONObject doInBackground(Object... arg0) {

            int responseCode = -1;
            JSONObject jsonResponse = null;
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://blog.teamtreehouse.com/api/get_recent_summary/?count=20");
            try{
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                responseCode = statusLine.getStatusCode();
                Log.v(TAG,"ResponseCode: "+responseCode);
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while( (line = reader.readLine()) != null ) {
                        builder.append(line);
                    }

                    jsonResponse = new JSONObject(builder.toString());

                }
                else {
                    Log.i(TAG,"Unsuccessful response code"+responseCode);
                }

            }
            catch (MalformedURLException e){
                Log.e(TAG,"MALFORException caught: "+ e.getMessage());

            }
            catch(IOException e) {
                Log.e(TAG,"ioException caught: "+ e.getMessage());
            }
            catch(Exception e) {
                Log.e(TAG,"GENERIC Exception caught: "+e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);

            mBlogData = jsonResponse;
            updateList();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
