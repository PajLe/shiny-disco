package data;

public class Rating {
    private double rating;
    private String ratedById;
    private String ratedDiscoId;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRatedById() {
        return ratedById;
    }

    public void setRatedById(String ratedById) {
        this.ratedById = ratedById;
    }

    public String getRatedDiscoId() {
        return ratedDiscoId;
    }

    public void setRatedDiscoId(String ratedDiscoId) {
        this.ratedDiscoId = ratedDiscoId;
    }
}
