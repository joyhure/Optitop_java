package com.optitop.optitop_api.dto;

import java.time.LocalDate;

public class QuotationDTO {
    private final Long id;
    private LocalDate date;
    private String seller;
    private String client;
    private String action;
    private String comment;

    // Constructeur avec id obligatoire
    public QuotationDTO(Long id) {
        this.id = id;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSeller() {
        return seller;
    }

    public String getClient() {
        return client;
    }

    public String getAction() {
        return action;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}