package com.example.samsophias.popularmoviesstage2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    AlertDialog alertDialog1;
    CharSequence[] values = {" Popular ", " Top Rated ", " Favorites "};
    private ArrayList<String> posters;
    private ArrayList<Integer> ids;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rec);

        Intent i = getIntent();
        String sort_type = i.getStringExtra("sort_type");
        if (sort_type == null) sort_type = "popular";
        new FetchMovies().execute(sort_type);


    }

    private void showFavorites() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort:
                CreateAlertDialogWithRadioButtonGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void CreateAlertDialogWithRadioButtonGroup() {


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Select Sorting Type");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        i.putExtra("sort_type", "popular");
                        i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(i);
                        break;
                    case 1:
                        Intent j = new Intent(MainActivity.this, MainActivity.class);
                        j.putExtra("sort_type", "top_rated");
                        j.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(j);
                        break;
                    case 2:
                        Intent k = new Intent(MainActivity.this, MainActivity.class);
                        k.putExtra("sort_type", "favorites");
                        k.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(k);
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }

    public class FetchMovies extends AsyncTask<String, Void, Void> {

        String LOG_TAG = "FetchMovies";

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            posters = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            Movie.movieList = new ArrayList<>();

            if(params[0].equals("favorites"))
            {
                Uri favorites = Uri.parse("content://com.example.samsophias.popularmoviesstage2/favorites");
                Cursor c = getContentResolver().query(favorites, null, null, null, "_id");

                if (c.moveToFirst()) {
                    do {
                        Movie mov = new Movie();
                        mov.setTitle(c.getString(c.getColumnIndex(FavoriteDatabaseProvider.TITLE)));
                        mov.setPoster_path(c.getString(c.getColumnIndex(FavoriteDatabaseProvider.POSTER_PATH)));
                        mov.setOverview(c.getString(c.getColumnIndex(FavoriteDatabaseProvider.SYNOPSIS)));
                        mov.setRelease_date(c.getString(c.getColumnIndex(FavoriteDatabaseProvider.RELEASE_DATE)));
                        mov.setRatings(Double.parseDouble(c.getString(c.getColumnIndex(FavoriteDatabaseProvider.USER_RATING))));
                        mov.setId(Integer.parseInt(c.getString(c.getColumnIndex(FavoriteDatabaseProvider._ID))));
                        Movie.movieList.add(mov);
                        ids.add(mov.getId());
                        posters.add(mov.getPoster_path());
                    } while (c.moveToNext());
                }
            }
            else
            {
                try {
                    String base_url = "https://api.themoviedb.org/3/movie/";
                    URL url = new URL(base_url + params[0] + "?api_key=" + "0281235d54dfc29087eb2111426c872a");
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
                    moviesJsonStr = buffer.toString();
                    Log.d(LOG_TAG, "JSON Parsed: " + moviesJsonStr);

                    JSONObject main = new JSONObject(moviesJsonStr);
                    JSONArray arr = main.getJSONArray("results");
                    JSONObject movie, id;
                    for (int i = 0; i < arr.length(); i++) {
                        movie = arr.getJSONObject(i);
                        Movie mov = new Movie();
                        mov.setTitle(movie.getString("title"));
                        mov.setPoster_path(movie.getString("poster_path"));
                        mov.setOverview(movie.getString("overview"));
                        mov.setRelease_date(movie.getString("release_date"));
                        mov.setRatings(movie.getDouble("vote_average"));
                        mov.setId(movie.getInt("id"));
                        Movie.movieList.add(mov);
                        ids.add(movie.getInt("id"));
                        posters.add(movie.getString("poster_path"));
                    }
                    Log.d(LOG_TAG, "Posters:" + posters);
                    Log.d(LOG_TAG, "IDs:" + ids);

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
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            MyAdapter myAdapter = new MyAdapter(posters, new OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    Intent i = new Intent(MainActivity.this, DetailActivity.class);
                    i.putExtra("position", position);
                    startActivity(i);
                }
            });
            recyclerView.setAdapter(myAdapter);

        }
    }
}
