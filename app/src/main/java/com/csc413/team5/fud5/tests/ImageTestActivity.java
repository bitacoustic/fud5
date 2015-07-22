package com.csc413.team5.fud5.tests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import com.csc413.team5.fud5.R;

import com.csc413.team5.restaurantapiwrapper.*;

import java.io.InputStream;
import java.net.URL;

public class ImageTestActivity extends AppCompatActivity {
    private TextView imageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);
        setTitle("Image Test");

        imageInfo = (TextView) findViewById(R.id.imageInfo);
        imageInfo.setText("Nothing yet!");
    }
    public void btnImageMe(View v){
        GetResultTask task = new GetResultTask();
        task.execute();
    }
    private class GetResultTask extends AsyncTask<String, Void, RestaurantList> {
        protected void onPostExecute(RestaurantList result) {
            //imageInfo.getEditableText().clear();
            LoadImageListTask task = new LoadImageListTask();
            task.execute(result);
        }
        @Override
        protected RestaurantList doInBackground(String... params)  {
            // Construct a YelpApiKey from Resource strings
            String consumerKey = getApplicationContext().getResources()
                    .getString(R.string.yelp_consumer_key);
            String consumerSecret = getApplicationContext().getResources()
                    .getString(R.string.yelp_consumer_secret);
            String tokenKey = getApplicationContext().getResources()
                    .getString(R.string.yelp_token);
            String tokenSecret = getApplicationContext().getResources()
                    .getString(R.string.yelp_token_secret);
            YelpApiKey yelpKey = new YelpApiKey(consumerKey, consumerSecret, tokenKey, tokenSecret);

            String location = ((EditText) findViewById(R.id.txtTestLocation)).getText().toString();
            String searchTerm = ((EditText) findViewById(R.id.txtTestSearchTerm)).getText().toString();
            try {
                RestaurantApiClient rClient = new RestaurantApiClient.Builder(yelpKey).location(location)
                        //.categoryFilter("foodtrucks,restaurants")
                        .term(searchTerm)
                                //.radiusFilter(15000)
                        .build();
                return rClient.getRestaurantList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private class LoadImageListTask extends AsyncTask<RestaurantList, String, String> {
        @Override
        protected void onProgressUpdate (String... values)
        {
            imageInfo.append(values[0]);
        }
        @Override
        protected String doInBackground(RestaurantList... params)  {
            RestaurantList results = params[0];
            for(int i = 0; i<results.size();i++)
            {
                try {
                    Restaurant restaurant = results.getRestaurant(i);
                    publishProgress(
                            "\nName = " + restaurant.getBusinessName() +
                            "\nhasImage = " + restaurant.hasImageUrl()
                    );
                    if(restaurant.hasImageUrl() == false) continue;
                    String imageUrl = restaurant.getImageUrl().toString();
                    imageUrl =  imageUrl.replace("ms.jpg","o.jpg"); //this gets original image size
                    URL url = new URL(imageUrl);
                    InputStream is = url.openConnection().getInputStream();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Bitmap b = BitmapFactory.decodeStream(is, null, options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    String imageType = options.outMimeType;
                    publishProgress(
                            "\nimageHeight = " + options.outHeight +
                            " imageWidth = " + options.outWidth +
                            " imageType = " + options.outMimeType +
                            "\n-----------------------------------"
                    );

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return "done";
        }
    }

}
