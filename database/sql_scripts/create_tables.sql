-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : ven. 18 avr. 2025 à 15:16
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `optitop`
--

-- --------------------------------------------------------

--
-- Structure de la table `email_config`
--

CREATE TABLE `email_config` (
  `id` int(11) NOT NULL,
  `smtp_host` varchar(255) NOT NULL,
  `smtp_port` int(11) NOT NULL,
  `smtp_username` varchar(255) NOT NULL,
  `smtp_password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `invoices`
--

CREATE TABLE `invoices` (
  `id` bigint(20) NOT NULL,
  `invoice_ref` varchar(255) NOT NULL,
  `client` varchar(255) NOT NULL,
  `client_id` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `seller_ref` varchar(50) DEFAULT NULL,
  `total_invoice` double NOT NULL,
  `status` varchar(255) NOT NULL,
  `is_optical` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `invoices_lines`
--

CREATE TABLE `invoices_lines` (
  `id` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `client_id` varchar(255) NOT NULL,
  `client` varchar(255) NOT NULL,
  `invoice_ref` varchar(255) NOT NULL,
  `family` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `total_ttc` double NOT NULL,
  `seller_ref` varchar(50) NOT NULL,
  `total_invoice` double NOT NULL,
  `pair` int(11) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `pending_accounts`
--

CREATE TABLE `pending_accounts` (
  `id` int(10) UNSIGNED NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `login` varchar(50) NOT NULL,
  `role` enum('admin','collaborator','manager','supermanager') NOT NULL,
  `request_type` enum('ajout','modification','suppression') NOT NULL,
  `created_by_user_id` int(10) UNSIGNED NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `quotations`
--

CREATE TABLE `quotations` (
  `id` bigint(20) NOT NULL,
  `client` varchar(255) NOT NULL,
  `client_id` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `date` date NOT NULL,
  `seller_ref` varchar(50) DEFAULT NULL,
  `is_validated` tinyint(1) NOT NULL DEFAULT 0,
  `action` enum('ATTENTE_MUTUELLE','ATTENTE_RETOUR','A_RELANCER','NON_VALIDE','VOIR_OPTICIEN') DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `quotations_lines`
--

CREATE TABLE `quotations_lines` (
  `id` bigint(20) NOT NULL,
  `client` varchar(255) NOT NULL,
  `client_id` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `date` date NOT NULL,
  `family` varchar(255) DEFAULT NULL,
  `pair` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `quotation_ref` varchar(255) NOT NULL,
  `seller_ref` varchar(50) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `total_quotation` double NOT NULL,
  `total_ttc` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `seller`
--

CREATE TABLE `seller` (
  `id` int(10) UNSIGNED NOT NULL,
  `seller_ref` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `user_id` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `id` int(10) UNSIGNED NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','collaborator','manager','supermanager') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `email_config`
--
ALTER TABLE `email_config`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_invoice_ref` (`invoice_ref`),
  ADD KEY `fk_invoices_seller_ref` (`seller_ref`);

--
-- Index pour la table `invoices_lines`
--
ALTER TABLE `invoices_lines`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_invoices_lines_seller` (`seller_ref`),
  ADD KEY `invoices_lines_invoice_ref` (`invoice_ref`);

--
-- Index pour la table `pending_accounts`
--
ALTER TABLE `pending_accounts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `request_unicity_by_account` (`login`),
  ADD KEY `fk_pending_account_created_by` (`created_by_user_id`);

--
-- Index pour la table `quotations`
--
ALTER TABLE `quotations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_quotations_seller_ref` (`seller_ref`);

--
-- Index pour la table `quotations_lines`
--
ALTER TABLE `quotations_lines`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_quotations_lines_seller` (`seller_ref`);

--
-- Index pour la table `seller`
--
ALTER TABLE `seller`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `seller_ref` (`seller_ref`),
  ADD UNIQUE KEY `fk_seller_user` (`user_id`) USING BTREE;

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `email_config`
--
ALTER TABLE `email_config`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `invoices_lines`
--
ALTER TABLE `invoices_lines`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `pending_accounts`
--
ALTER TABLE `pending_accounts`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `quotations`
--
ALTER TABLE `quotations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `quotations_lines`
--
ALTER TABLE `quotations_lines`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `seller`
--
ALTER TABLE `seller`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `fk_invoices_invoice_ref` FOREIGN KEY (`invoice_ref`) REFERENCES `invoices_lines` (`invoice_ref`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_invoices_seller_ref` FOREIGN KEY (`seller_ref`) REFERENCES `seller` (`seller_ref`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `invoices_lines`
--
ALTER TABLE `invoices_lines`
  ADD CONSTRAINT `fk_invoices_lines_seller` FOREIGN KEY (`seller_ref`) REFERENCES `seller` (`seller_ref`);

--
-- Contraintes pour la table `pending_accounts`
--
ALTER TABLE `pending_accounts`
  ADD CONSTRAINT `fk_pending_account_created_by` FOREIGN KEY (`created_by_user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE;

--
-- Contraintes pour la table `quotations`
--
ALTER TABLE `quotations`
  ADD CONSTRAINT `fk_quotations_seller_ref` FOREIGN KEY (`seller_ref`) REFERENCES `seller` (`seller_ref`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Contraintes pour la table `quotations_lines`
--
ALTER TABLE `quotations_lines`
  ADD CONSTRAINT `fk_quotations_lines_seller` FOREIGN KEY (`seller_ref`) REFERENCES `seller` (`seller_ref`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Contraintes pour la table `seller`
--
ALTER TABLE `seller`
  ADD CONSTRAINT `fk_seller_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
