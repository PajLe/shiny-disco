package data;

import android.net.Uri;

import java.util.HashMap;

public class User {
    private String uid;
    private String username;
    private String password; // password stored for testing purposes
    private String email;
    private String name;
    private String imageUrl;
    private String rank;
    private int rankPoints; // 0-2 newbie; 3-5 party guy; 6+ party monster
    private double lat;
    private double lon;
    private HashMap<String, Boolean> friends;

    public User() {
        updateRankBasedOnCurrentPoints();
    }

    public int getRankPoints() {
        return rankPoints;
    }

    public void setRankPoints(int rankPoints) {
        this.rankPoints = rankPoints;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void increaseRankPoints() {
        rankPoints++;
        updateRankBasedOnCurrentPoints();
    }

    private void updateRankBasedOnCurrentPoints() {
        if (rankPoints < 3)
            rank = "Newbie";
        else if (rankPoints < 6)
            rank = "Party guy";
        else
            rank = "Party monster";
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public HashMap<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, Boolean> friends) {
        this.friends = friends;
    }
}
