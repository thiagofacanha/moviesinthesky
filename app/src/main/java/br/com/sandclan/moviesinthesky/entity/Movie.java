package br.com.sandclan.moviesinthesky.entity;

import java.io.Serializable;

public class Movie implements Serializable {
    public static final String HTTP_IMAGE_TMDB_ORG_T_P_W500 = "http://image.tmdb.org/t/p/w500";
    private int id;
    private String title;
    private String original_title;
    private String imageUrl;
    private String synopsis;
    private Double voteAverage;
    private String releaseDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return HTTP_IMAGE_TMDB_ORG_T_P_W500 + imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }
}


