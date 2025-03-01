package dto;

public class QuotationUpdateDTO {
    private Long id;
    private String action;
    private String comment;

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
    public void setId(Long id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}