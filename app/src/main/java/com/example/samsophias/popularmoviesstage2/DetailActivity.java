package com.example.samsophias.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private Movie selectedMovie;
    private ImageView imgThumb;
    private String[] youtubeIds;
    private String[] trailerList;
    private int trailer_count;
    private Button btnShare;
    private RecyclerView recyclerTrailer, recyclerReview;
    private JSONArray reviews;
    private int review_count;
    private List<String> listReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Movie Details");
        Intent i = getIntent();
        int position = i.getIntExtra("position", -1);
        selectedMovie = Movie.movieList.get(position);

        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtRelease = findViewById(R.id.txtRelease);
        TextView txtRatings = findViewById(R.id.txtRatings);
        TextView txtOverview = findViewById(R.id.txtOverview);
        imgThumb = findViewById(R.id.imgThumbnail);
        recyclerTrailer = findViewById(R.id.recyclerTrailer);
        recyclerReview = findViewById(R.id.recyclerReview);
        Button btnFavorites = findViewById(R.id.btnFavorite);
        btnShare = findViewById(R.id.btnShare);

        btnShare.setVisibility(View.INVISIBLE);
        txtTitle.setText(selectedMovie.getTitle());
        txtRelease.setText("Release Date : " + selectedMovie.getRelease_date());
        txtRatings.setText("User Ratings : " + selectedMovie.getRatings().toString());
        txtOverview.setText(selectedMovie.getOverview());
        Picasso.with(this).load("https://image.tmdb.org/t/p/w185" + selectedMovie.getPoster_path()).into(imgThumb);

        new FetchMovies().execute();

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorites();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if(trailer_count != 0)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + youtubeIds[0]);
        startActivity(Intent.createChooser(shareIntent, "Share Trailer To"));
    }

    private void addToFavorites() {
        ContentValues values = new ContentValues();
        values.put(FavoriteDatabaseProvider._ID, selectedMovie.getId());
        values.put(FavoriteDatabaseProvider.TITLE, selectedMovie.getTitle());
        values.put(FavoriteDatabaseProvider.SYNOPSIS, selectedMovie.getOverview());
        values.put(FavoriteDatabaseProvider.USER_RATING, selectedMovie.getRatings());
        values.put(FavoriteDatabaseProvider.RELEASE_DATE, selectedMovie.getRelease_date());
        values.put(FavoriteDatabaseProvider.POSTER_PATH, selectedMovie.getPoster_path());
        BitmapDrawable drawable = (BitmapDrawable) imgThumb.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        values.put(FavoriteDatabaseProvider.POSTER, image);

        Uri uri = getContentResolver().insert(FavoriteDatabaseProvider.CONTENT_URI, values);
        Log.d("TEST", uri.toString());

        if(uri.toString().equals("Duplicate"))
            Toast.makeText(this, R.string.favorite_exists, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.favorite_success, Toast.LENGTH_SHORT).show();
    }


    public class FetchMovies extends AsyncTask<String, Void, Void> {

        String LOG_TAG = "FetchMovies";

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + selectedMovie.getId() + "/videos" + "?api_key=" + "0281235d54dfc29087eb2111426c872a");
                Log.d(LOG_TAG, "URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                String trailerJsonStr = buffer.toString();
                Log.d(LOG_TAG, "JSON Parsed: " + trailerJsonStr);

                JSONObject main = new JSONObject(trailerJsonStr);
                String results = main.getString("results");
                JSONArray trailers = new JSONArray(results);
                trailer_count = trailers.length();
                Log.d(LOG_TAG, "Number of Trailers:" + trailer_count);

                //Ensure there is at least one trailer
                if (trailer_count != 0) {
                    youtubeIds = new String[trailer_count];
                    trailerList = new String[trailer_count];
                    for (int i = 0; i < trailer_count; i++) {
                        JSONObject obj = trailers.getJSONObject(i);
                        youtubeIds[i] = obj.getString("key");
                        trailerList[i] = "http://www.youtube.com/watch?v=" + youtubeIds[i];
                    }
                }

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //get reviews
            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + selectedMovie.getId() + "/reviews" + "?api_key=" + "0281235d54dfc29087eb2111426c872a");
                Log.d(LOG_TAG, "URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                String reviewJsonStr = buffer.toString();
                Log.d(LOG_TAG, "JSON Parsed: " + reviewJsonStr);

                JSONObject main = new JSONObject(reviewJsonStr);
                String results = main.getString("results");
                reviews = new JSONArray(results);
                review_count = main.getInt("total_results");

                listReview = new ArrayList<>();
                for(int i = 0; i < review_count; i++){
                    listReview.add(reviews.getJSONObject(i).getString("content"));
                }
                Log.d(LOG_TAG, "Number of Reviews:" + review_count);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            btnShare.setVisibility(View.VISIBLE);
            recyclerTrailer.setNestedScrollingEnabled(false);
            recyclerReview.setNestedScrollingEnabled(false);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.VERTICAL, false);
            RecyclerView.LayoutManager layoutManagerReview = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.VERTICAL, false);

            recyclerTrailer.setLayoutManager(layoutManager);
            recyclerReview.setLayoutManager(layoutManagerReview);
            TrailerAdapter adapter = new TrailerAdapter(youtubeIds);
            ReviewAdapter reviewAdapter = new ReviewAdapter(listReview);
            recyclerTrailer.setAdapter(adapter);
            recyclerReview.setAdapter(reviewAdapter);

        }
    }
}
