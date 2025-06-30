package org.sky.study.dto;

public class TopRatedMovie {
    private String name;
    private Double averageRating;

    public TopRatedMovie(String name, Double averageRating) {
        this.name = name;
        this.averageRating = averageRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
