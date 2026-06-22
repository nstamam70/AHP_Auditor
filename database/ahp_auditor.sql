-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jun 22, 2026 at 11:00 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ahp_auditor`
--

-- --------------------------------------------------------

--
-- Table structure for table `auditor`
--

CREATE TABLE `auditor` (
  `id_auditor` int(11) NOT NULL,
  `kode_auditor` varchar(20) NOT NULL,
  `nama_auditor` varchar(100) NOT NULL,
  `jabatan` varchar(100) DEFAULT NULL,
  `status` enum('Aktif','Nonaktif') NOT NULL DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `auditor`
--

INSERT INTO `auditor` (`id_auditor`, `kode_auditor`, `nama_auditor`, `jabatan`, `status`) VALUES
(1, 'ADT-001', 'Putra Ramadhani', 'Auditor', 'Aktif'),
(2, 'ADT-002', 'Mia Anggita', 'Audit', 'Aktif'),
(3, 'ADT-003', 'Agung Pramana Mulia Ady', 'Audit', 'Aktif'),
(4, 'ADT-004', 'Andre Setiawan', 'Audit', 'Aktif'),
(5, 'ADT-005', 'Dinda Safitri', 'Audit', 'Aktif');

-- --------------------------------------------------------

--
-- Table structure for table `hasil_bobot_alternatif`
--

CREATE TABLE `hasil_bobot_alternatif` (
  `id` int(11) NOT NULL,
  `id_proses` int(11) NOT NULL,
  `id_kriteria` int(11) NOT NULL,
  `id_auditor` int(11) NOT NULL,
  `bobot` double NOT NULL,
  `cr` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `hasil_bobot_kriteria`
--

CREATE TABLE `hasil_bobot_kriteria` (
  `id` int(11) NOT NULL,
  `id_proses` int(11) NOT NULL,
  `id_kriteria` int(11) NOT NULL,
  `bobot` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `hasil_ranking`
--

CREATE TABLE `hasil_ranking` (
  `id` int(11) NOT NULL,
  `id_proses` int(11) NOT NULL,
  `id_auditor` int(11) NOT NULL,
  `nilai_global` double NOT NULL,
  `ranking` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `kriteria`
--

CREATE TABLE `kriteria` (
  `id_kriteria` int(11) NOT NULL,
  `kode_kriteria` varchar(10) NOT NULL,
  `nama_kriteria` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kriteria`
--

INSERT INTO `kriteria` (`id_kriteria`, `kode_kriteria`, `nama_kriteria`) VALUES
(1, 'KRT-001', 'Kompetensi Auditor'),
(2, 'KRT-002', 'Persiapan Audit'),
(3, 'KRT-003', 'Ketepatan Waktu'),
(4, 'KRT-004', 'Kemampuan Identifikasi Temuan'),
(5, 'KRT-005', 'Kualitas Laporan Audit'),
(6, 'KRT-006', 'Komunikasi dan Wawancara'),
(7, 'KRT-007', 'Kerja Sama Tim'),
(8, 'KRT-008', 'Kepuasan Auditee');

-- --------------------------------------------------------

--
-- Table structure for table `perbandingan_alternatif`
--

CREATE TABLE `perbandingan_alternatif` (
  `id_perbandingan` int(11) NOT NULL,
  `id_kriteria` int(11) NOT NULL,
  `id_auditor_1` int(11) NOT NULL,
  `id_auditor_2` int(11) NOT NULL,
  `nilai` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `perbandingan_kriteria`
--

CREATE TABLE `perbandingan_kriteria` (
  `id_perbandingan` int(11) NOT NULL,
  `id_kriteria_1` int(11) NOT NULL,
  `id_kriteria_2` int(11) NOT NULL,
  `nilai` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `proses_ahp`
--

CREATE TABLE `proses_ahp` (
  `id_proses` int(11) NOT NULL,
  `nama_proses` varchar(100) NOT NULL,
  `tanggal_proses` datetime NOT NULL DEFAULT current_timestamp(),
  `cr_kriteria` double DEFAULT NULL,
  `status_konsistensi` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id_user`, `username`, `password`) VALUES
(1, 'admin', 'admin123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `auditor`
--
ALTER TABLE `auditor`
  ADD PRIMARY KEY (`id_auditor`),
  ADD UNIQUE KEY `kode_auditor` (`kode_auditor`);

--
-- Indexes for table `hasil_bobot_alternatif`
--
ALTER TABLE `hasil_bobot_alternatif`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_proses` (`id_proses`),
  ADD KEY `id_kriteria` (`id_kriteria`),
  ADD KEY `id_auditor` (`id_auditor`);

--
-- Indexes for table `hasil_bobot_kriteria`
--
ALTER TABLE `hasil_bobot_kriteria`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_proses` (`id_proses`),
  ADD KEY `id_kriteria` (`id_kriteria`);

--
-- Indexes for table `hasil_ranking`
--
ALTER TABLE `hasil_ranking`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_proses` (`id_proses`),
  ADD KEY `id_auditor` (`id_auditor`);

--
-- Indexes for table `kriteria`
--
ALTER TABLE `kriteria`
  ADD PRIMARY KEY (`id_kriteria`),
  ADD UNIQUE KEY `kode_kriteria` (`kode_kriteria`);

--
-- Indexes for table `perbandingan_alternatif`
--
ALTER TABLE `perbandingan_alternatif`
  ADD PRIMARY KEY (`id_perbandingan`),
  ADD KEY `id_kriteria` (`id_kriteria`),
  ADD KEY `id_auditor_1` (`id_auditor_1`),
  ADD KEY `id_auditor_2` (`id_auditor_2`);

--
-- Indexes for table `perbandingan_kriteria`
--
ALTER TABLE `perbandingan_kriteria`
  ADD PRIMARY KEY (`id_perbandingan`),
  ADD KEY `id_kriteria_1` (`id_kriteria_1`),
  ADD KEY `id_kriteria_2` (`id_kriteria_2`);

--
-- Indexes for table `proses_ahp`
--
ALTER TABLE `proses_ahp`
  ADD PRIMARY KEY (`id_proses`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `auditor`
--
ALTER TABLE `auditor`
  MODIFY `id_auditor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `hasil_bobot_alternatif`
--
ALTER TABLE `hasil_bobot_alternatif`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `hasil_bobot_kriteria`
--
ALTER TABLE `hasil_bobot_kriteria`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `hasil_ranking`
--
ALTER TABLE `hasil_ranking`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `kriteria`
--
ALTER TABLE `kriteria`
  MODIFY `id_kriteria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `perbandingan_alternatif`
--
ALTER TABLE `perbandingan_alternatif`
  MODIFY `id_perbandingan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `perbandingan_kriteria`
--
ALTER TABLE `perbandingan_kriteria`
  MODIFY `id_perbandingan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `proses_ahp`
--
ALTER TABLE `proses_ahp`
  MODIFY `id_proses` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `hasil_bobot_alternatif`
--
ALTER TABLE `hasil_bobot_alternatif`
  ADD CONSTRAINT `hasil_bobot_alternatif_ibfk_1` FOREIGN KEY (`id_proses`) REFERENCES `proses_ahp` (`id_proses`) ON DELETE CASCADE,
  ADD CONSTRAINT `hasil_bobot_alternatif_ibfk_2` FOREIGN KEY (`id_kriteria`) REFERENCES `kriteria` (`id_kriteria`) ON DELETE CASCADE,
  ADD CONSTRAINT `hasil_bobot_alternatif_ibfk_3` FOREIGN KEY (`id_auditor`) REFERENCES `auditor` (`id_auditor`) ON DELETE CASCADE;

--
-- Constraints for table `hasil_bobot_kriteria`
--
ALTER TABLE `hasil_bobot_kriteria`
  ADD CONSTRAINT `hasil_bobot_kriteria_ibfk_1` FOREIGN KEY (`id_proses`) REFERENCES `proses_ahp` (`id_proses`) ON DELETE CASCADE,
  ADD CONSTRAINT `hasil_bobot_kriteria_ibfk_2` FOREIGN KEY (`id_kriteria`) REFERENCES `kriteria` (`id_kriteria`) ON DELETE CASCADE;

--
-- Constraints for table `hasil_ranking`
--
ALTER TABLE `hasil_ranking`
  ADD CONSTRAINT `hasil_ranking_ibfk_1` FOREIGN KEY (`id_proses`) REFERENCES `proses_ahp` (`id_proses`) ON DELETE CASCADE,
  ADD CONSTRAINT `hasil_ranking_ibfk_2` FOREIGN KEY (`id_auditor`) REFERENCES `auditor` (`id_auditor`) ON DELETE CASCADE;

--
-- Constraints for table `perbandingan_alternatif`
--
ALTER TABLE `perbandingan_alternatif`
  ADD CONSTRAINT `perbandingan_alternatif_ibfk_1` FOREIGN KEY (`id_kriteria`) REFERENCES `kriteria` (`id_kriteria`) ON DELETE CASCADE,
  ADD CONSTRAINT `perbandingan_alternatif_ibfk_2` FOREIGN KEY (`id_auditor_1`) REFERENCES `auditor` (`id_auditor`) ON DELETE CASCADE,
  ADD CONSTRAINT `perbandingan_alternatif_ibfk_3` FOREIGN KEY (`id_auditor_2`) REFERENCES `auditor` (`id_auditor`) ON DELETE CASCADE;

--
-- Constraints for table `perbandingan_kriteria`
--
ALTER TABLE `perbandingan_kriteria`
  ADD CONSTRAINT `perbandingan_kriteria_ibfk_1` FOREIGN KEY (`id_kriteria_1`) REFERENCES `kriteria` (`id_kriteria`) ON DELETE CASCADE,
  ADD CONSTRAINT `perbandingan_kriteria_ibfk_2` FOREIGN KEY (`id_kriteria_2`) REFERENCES `kriteria` (`id_kriteria`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
