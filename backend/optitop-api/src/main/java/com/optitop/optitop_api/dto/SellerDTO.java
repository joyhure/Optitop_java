package com.optitop.optitop_api.dto;

/**
 * DTO pour les informations des vendeurs
 * 
 * Classe de transfert de données pour transporter les informations
 * des vendeurs disponibles pour création de compte utilisateur.
 */
public class SellerDTO {

        // ===== PROPRIÉTÉS =====

        /**
         * Référence unique du vendeur
         */
        private String sellerRef;

        // ===== CONSTRUCTEURS =====

        /**
         * Constructeur par défaut
         * Requis pour la désérialisation JSON par Spring Boot
         */
        public SellerDTO() {
        }

        /**
         * Constructeur avec paramètre
         * 
         * @param sellerRef Référence du vendeur
         */
        public SellerDTO(String sellerRef) {
                this.sellerRef = sellerRef;
        }

        // ===== GETTERS ET SETTERS =====

        /**
         * @return Référence unique du vendeur
         */
        public String getSellerRef() {
                return sellerRef;
        }

        /**
         * @param sellerRef Référence du vendeur à définir
         */
        public void setSellerRef(String sellerRef) {
                this.sellerRef = sellerRef;
        }
}