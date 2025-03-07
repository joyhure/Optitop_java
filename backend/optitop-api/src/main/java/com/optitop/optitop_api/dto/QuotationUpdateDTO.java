package com.optitop.optitop_api.dto;

public class QuotationUpdateDTO {
    private final Long id;
    private String action;
    private String comment;

    // Constructeur avec id obligatoire
    public QuotationUpdateDTO(Long id) {
        this.id = id;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setAction(String action) {
        this.action = action;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}