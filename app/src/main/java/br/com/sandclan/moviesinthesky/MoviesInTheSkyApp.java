package br.com.sandclan.moviesinthesky;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MoviesInTheSkyApp extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}