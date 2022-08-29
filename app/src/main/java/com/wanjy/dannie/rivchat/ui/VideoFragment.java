package com.wanjy.dannie.rivchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.Toast;

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.Videos;
import com.google.android.youtube.player.YouTubeStandalonePlayer;


/**
 * Created by christophercoffee on 12/17/16.
 */

public class VideoFragment extends Fragment {


    List<Videos> displaylistArray = new ArrayList<Videos>();
    private RecyclerView mVideoRecyclerView;
    private YT_recycler_adapter mVideoAdapter;
    Context context;
    private String playlist_id;
    private String browserKey;
    String loadMsg;
    String loadTitle;


    //onCreateView...
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //show life-cycle event in LogCat console...
		context = getActivity().getApplicationContext();
        View thisScreensView = inflater.inflate(R.layout.video_fragment, container, false);


        playlist_id = "PLLV9YTAPSMHhdgeR4B-75lDsbpgKkAfDD";
        int videoTxtColor = Color.parseColor("#000000");
        loadTitle = "Loading...";
        loadMsg = "Loading your videos...";
        browserKey = "AIzaSyAweHHScvQJiui2hMyKkqcyahCQ2bJfZPs";
        int cornerRadius = 5;
        int cardColor = Color.parseColor("#FFFFFF");
        int recyclerColor = Color.parseColor("#ff0000");


        mVideoRecyclerView = (RecyclerView) thisScreensView.findViewById(R.id.recycleListFriend);
        mVideoRecyclerView.setBackgroundColor(recyclerColor);
        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mVideoAdapter = new YT_recycler_adapter(displaylistArray, browserKey, getActivity(), cornerRadius, cardColor, videoTxtColor);
        mVideoRecyclerView.setAdapter(mVideoAdapter);

        new TheTask().execute();
        mVideoAdapter.notifyDataSetChanged();

        return thisScreensView;

    }
    private class TheTask extends AsyncTask<Void,Void,Void>
    {

        Videos displaylist;
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle(loadTitle);
            dialog.setMessage(loadMsg);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try
            {

                String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + playlist_id+ "&key=" + browserKey + "&maxResults=50";
                String response = getUrlString(url);
                JSONObject json = new JSONObject(response.toString());

                JSONArray jsonArray = json.getJSONArray("items");

				//JSONObject statistics = jsonArray.getJSONObject(0).getJSONObject("statistics");



                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    JSONObject video = jsonObject.getJSONObject("snippet").getJSONObject("resourceId");
                    String title = jsonObject.getJSONObject("snippet").getString("title");

					/*JSONObject statistics = jsonArray.getJSONObject(i).getJSONObject("statistics");
					 long views = statistics.getLong("viewCount");
					 */

					long views = 6;
                    String id = video.getString("videoId");
                    String thumbUrl = jsonObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");

                    displaylist = new Videos(title, thumbUrl ,id,views);
                    displaylistArray.add(displaylist);
                }

            }
            catch(Exception e1)
            {
                e1.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mVideoAdapter.notifyDataSetChanged();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }



    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
									  ": with " +
									  urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

	//Adapter /////////////

	public class YT_recycler_adapter extends RecyclerView.Adapter<YT_recycler_adapter.MyViewHolder> {

		private List<Videos> videoList;
		String key;
		Activity activity;
		private int REQ_PLAYER_CODE  = 1;
		int cornerRadius;
		int cardColor;
		int textColor;

		public class MyViewHolder extends RecyclerView.ViewHolder {
			public TextView name;
			public ImageView imageView;

			public MyViewHolder(View view) {
				super(view);

				imageView = (ImageView)view.findViewById(R.id.imageView) ;
				view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Videos video = videoList.get(getAdapterPosition());
							Intent videoIntent = YouTubeStandalonePlayer.createVideoIntent(activity, key, video.getVideoID(), 0, true, false);
							activity.startActivityForResult(videoIntent, REQ_PLAYER_CODE);

						}
					});


				CardView cardView = (CardView) view.findViewById(R.id.card_view);
				cardView.setCardBackgroundColor(cardColor);
				cardView.setRadius(cornerRadius);


				name = (TextView) view.findViewById(R.id.name);
				name.setTextColor(textColor);
				name.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});

			}
		}

		public YT_recycler_adapter(List<Videos> videoList, String yt_key, Activity activity, int cornerRadius, int cardColor, int textColor) {
			this.activity  = activity;
			this.key = yt_key;
			this.videoList = videoList;
			this.cornerRadius = cornerRadius;
			this.cardColor = cardColor;
			this.textColor = textColor;

		}

		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);

			return new MyViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(MyViewHolder holder, int position) {
			Videos video = videoList.get(position);
			holder.name.setText(video.getTitle());

			Glide.with(activity)
                .load(video.getThumbnailUrl() ) // Uri of the picture
                .into(holder.imageView);

		}

		@Override
		public int getItemCount() {
			return videoList.size();
		}
	}

}
