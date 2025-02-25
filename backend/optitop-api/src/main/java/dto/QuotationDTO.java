package dto;

import java.time.LocalDate;

public class QuotationDTO {
    private LocalDate date;
    private String sellerRef;
    private String client;
    private String action;
    private String comment;

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public String getSellerRef() {
        return sellerRef;
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

    public void setSellerRef(String sellerRef) {
        this.sellerRef = sellerRef;
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