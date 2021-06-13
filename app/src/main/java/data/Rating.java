package data;

public class Rating {
    private double rating;
    private User ratedBy;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public User getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(User ratedBy) {
        this.ratedBy = ratedBy;
    }
}
