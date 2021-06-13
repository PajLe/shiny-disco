package data;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Disco {
    private String name;
    private LatLng position;
    private double lat;
    private double lon;
    private String pricing; // Budget;Average;Luxury
    private List<String> musicGenres; // take from select list
    private List<Rating> ratings;
    private double averageRating;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        if (position == null)
            return new LatLng(lat, lon);
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public List<String> getMusicGenres() {
        return musicGenres;
    }

    public void setMusicGenres(List<String> musicGenres) {
        this.musicGenres = musicGenres;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
