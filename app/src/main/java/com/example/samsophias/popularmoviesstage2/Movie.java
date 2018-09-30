package com.example.samsophias.popularmoviesstage2;

import java.util.List;

public class Movie {
    public static List<Movie> movieList;
    private String title;
    private String poster_path;
    private String release_date;
    private String overview;
    private Double ratings;
    private int id;


    public Movie(String title, String poster_path, String release_date, String overview, Double ratings, int id) {
        this.title = title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.overview = overview;
        this.ratings = ratings;
        this.id = id;
    }

    public Movie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
