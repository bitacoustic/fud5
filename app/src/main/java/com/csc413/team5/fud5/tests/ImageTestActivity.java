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
import android.text.format.Formatter;

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
            imageInfo.setText("");
            loadImages(result);
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


    private void loadImages(RestaurantList results)
    {
        for(int i = 0; i<results.size();i++)
        {
            try {
                final Restaurant restaurant = results.getRestaurant(i);
                if(restaurant.hasImageUrl() == false) continue;
                String imageUrl = restaurant.getImageUrl().toString();
                imageUrl =  imageUrl.replace("ms.jpg","o.jpg"); //this gets original image size
                URL url = new URL(imageUrl);

                LoadImageTask loadRestaurantImage = new LoadImageTask(){
                    protected void onPostExecute(Bitmap bitmap) {
                        imageInfo.append(
                                "\nName = " + restaurant.getBusinessName() +
                                "\nhasImage = " + restaurant.hasImageUrl()+
                                "\nResolution: " + bitmap.getHeight() +
                                " x " + bitmap.getWidth() +
                                " Bitmap Size: " + Formatter.formatFileSize(getApplicationContext(),
                                         bitmap.getAllocationByteCount()) +
                                "\n-----------------------------------"
                        );
                    }
                };
                loadRestaurantImage.execute(url);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public class LoadImageTask extends AsyncTask<URL, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground (URL... imageURL)
        {
            try {
                InputStream stream = imageURL[0].openConnection().getInputStream();
                return BitmapFactory.decodeStream(stream);
            }catch(Exception e) {}
            return null;
        }
    }
}
