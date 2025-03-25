package com.optitop.optitop_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.EnumType;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.ForeignKey;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_ref", referencedColumnName = "seller_ref", foreignKey = @ForeignKey(name = "fk_quotations_seller_ref"))
    private Seller seller;

    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private QuotationAction action;

    @Column(name = "comment")
    private String comment;

    public enum QuotationAction {
        VOIR_OPTICIEN("Voir opticien"),
        NON_VALIDE("Non validé"),
        ATTENTE_MUTUELLE("Attente mutuelle"),
        A_RELANCER("A relancer"),
        ATTENTE_RETOUR("Attente de retour");

        private final String value;

        QuotationAction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSellerRef() {
        return seller != null ? seller.getSellerRef() : null;
    }

    public void setSellerRef(String sellerRef) {
        // À gérer au niveau service
        this.seller = null;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public QuotationAction getAction() {
        return action;
    }

    public void setAction(QuotationAction action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}