package com.example.sagar.youtubetest;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerView youTubePlayerView;
    String API_KEY="AIzaSyD0MEFQoOetSQD2GdO1Gg9uuPP1OodYbag";
    private static final int RECOVERY_REQUEST = 1;
    String TAG="VideoActivity";

    String URL="https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCsTcErHg8oDvUnTzoqsYeNw&maxResults=50&q=surfing&key=AIzaSyD0MEFQoOetSQD2GdO1Gg9uuPP1OodYbag";

    ListView videoList2;
    ArrayList<VideoDetails> videoDetailsArrayList2;
    CustomListAdapter2 customListAdapter2;

    private void showVideo() {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    System.out.println("Data from url"+response);

                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("items");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId=jsonObject1.getJSONObject("id");
                        JSONObject jsonsnippet= jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        VideoDetails videoDetails=new VideoDetails();
                        System.out.println("VIDEO TITLE"+jsonsnippet.getString("title"));
                        System.out.println("VIDEO DESCRIPTION"+jsonsnippet.getString("description"));

                        String videoid=jsonVideoId.getString("videoId");

                        Log.e(TAG," New Video Id" +videoid);
                        videoDetails.setURL(jsonObjectdefault.getString("url"));
                        videoDetails.setVideoName(jsonsnippet.getString("title"));
                        videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                        videoDetails.setVideoId(videoid);

                        videoDetailsArrayList2.add(videoDetails);
                    }
                    videoList2.setAdapter(customListAdapter2);
                    // customListAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "onResponse: END OF FUNCTION ");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        youTubePlayerView=(YouTubePlayerView)findViewById(R.id.youtubeview);
        youTubePlayerView.initialize(API_KEY, this);

        videoList2=(ListView)findViewById(R.id.videoList2);
        videoDetailsArrayList2=new ArrayList<>();
        customListAdapter2=new CustomListAdapter2(VideoActivity.this,videoDetailsArrayList2);
        showVideo();
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        Bundle bundle = getIntent().getExtras();
        String showVideo = bundle.getString("videoId");
        Log.e(TAG,"Video" +showVideo);

        youTubePlayer.cueVideo(showVideo);


     //   youTubePlayer.cuePlaylist("PLoaVOjvkzQtyjhV55wZcdicAz5KexgKvm");



       // youTubePlayer.cueVideo("2zNSgSzhBfM");

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {

            Toast.makeText(this, "Error Initializing Youtube Player", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
    }

}