package com.example.projetofinal.model.dto;

public class GameDTO {

    private Integer id;
    private String name;
    private String description_raw;
    private String released;
    private String background_image;
    private Double rating;
    private Integer rating_top;

    public GameDTO() {
    }

    public GameDTO(Integer id, String name, String description_raw, String released, String background_image, Double rating, Integer rating_top) {
        this.id = id;
        this.name = name;
        this.description_raw = description_raw;
        this.released = released;
        this.background_image = background_image;
        this.rating = rating;
        this.rating_top = rating_top;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription_raw() {
        return description_raw;
    }

    public void setDescription_raw(String description_raw) {
        this.description_raw = description_raw;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRating_top() {
        return rating_top;
    }

    public void setRating_top(Integer rating_top) {
        this.rating_top = rating_top;
    }
}
