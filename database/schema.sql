-- Data seed perbandingan berpasangan sesuai BAB 4
-- Jalankan SETELAH ahp_auditor.sql diimport
-- Pastikan tabel perbandingan_kriteria dan perbandingan_alternatif sudah ada

USE ahp_auditor;

-- ===== PERBANDINGAN BERPASANGAN KRITERIA (Tabel 4.3) =====
-- Hanya simpan pasangan atas diagonal (i < j), resiprokal dihitung otomatis di Java
-- C1=1, C2=2, C3=3, C4=4, C5=5, C6=6, C7=7, C8=8

INSERT INTO perbandingan_kriteria (id_kriteria_1, id_kriteria_2, nilai) VALUES
-- C1 vs C2..C8
(1, 2, 3), (1, 3, 5), (1, 4, 0.5), (1, 5, 2), (1, 6, 4), (1, 7, 5), (1, 8, 7),
-- C2 vs C3..C8
(2, 3, 3), (2, 4, 0.25), (2, 5, 0.5), (2, 6, 2), (2, 7, 3), (2, 8, 5),
-- C3 vs C4..C8
(3, 4, 0.2), (3, 5, 0.333333), (3, 6, 1), (3, 7, 2), (3, 8, 3),
-- C4 vs C5..C8
(4, 5, 3), (4, 6, 5), (4, 7, 6), (4, 8, 7),
-- C5 vs C6..C8
(5, 6, 3), (5, 7, 4), (5, 8, 5),
-- C6 vs C7..C8
(6, 7, 2), (6, 8, 3),
-- C7 vs C8
(7, 8, 2);

-- ===== PERBANDINGAN BERPASANGAN ALTERNATIF =====

-- C1: Kompetensi Auditor (Tabel 4.9)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(1, 1, 2, 0.333333), (1, 1, 3, 3), (1, 1, 4, 0.5), (1, 1, 5, 4),
(1, 2, 3, 5), (1, 2, 4, 2), (1, 2, 5, 7),
(1, 3, 4, 0.333333), (1, 3, 5, 3),
(1, 4, 5, 5);

-- C2: Persiapan Audit (Tabel 4.14)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(2, 1, 2, 0.5), (2, 1, 3, 3), (2, 1, 4, 2), (2, 1, 5, 4),
(2, 2, 3, 4), (2, 2, 4, 3), (2, 2, 5, 5),
(2, 3, 4, 0.5), (2, 3, 5, 3),
(2, 4, 5, 4);

-- C3: Ketepatan Waktu (Tabel 4.18)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(3, 1, 2, 0.5), (3, 1, 3, 2), (3, 1, 4, 0.333333), (3, 1, 5, 4),
(3, 2, 3, 3), (3, 2, 4, 0.5), (3, 2, 5, 5),
(3, 3, 4, 0.25), (3, 3, 5, 3),
(3, 4, 5, 6);

-- C4: Kemampuan Identifikasi Temuan (Tabel 4.22)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(4, 1, 2, 0.5), (4, 1, 3, 3), (4, 1, 4, 2), (4, 1, 5, 4),
(4, 2, 3, 4), (4, 2, 4, 3), (4, 2, 5, 5),
(4, 3, 4, 0.5), (4, 3, 5, 3),
(4, 4, 5, 4);

-- C5: Kualitas Laporan Audit (Tabel 4.26)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(5, 1, 2, 2), (5, 1, 3, 4), (5, 1, 4, 0.5), (5, 1, 5, 5),
(5, 2, 3, 3), (5, 2, 4, 0.333333), (5, 2, 5, 4),
(5, 3, 4, 0.2), (5, 3, 5, 2),
(5, 4, 5, 6);

-- C6: Komunikasi dan Wawancara (Tabel 4.30)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(6, 1, 2, 0.5), (6, 1, 3, 3), (6, 1, 4, 2), (6, 1, 5, 4),
(6, 2, 3, 4), (6, 2, 4, 3), (6, 2, 5, 5),
(6, 3, 4, 0.5), (6, 3, 5, 3),
(6, 4, 5, 4);

-- C7: Kerja Sama Tim (Tabel 4.34)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(7, 1, 2, 0.5), (7, 1, 3, 3), (7, 1, 4, 0.5), (7, 1, 5, 4),
(7, 2, 3, 4), (7, 2, 4, 2), (7, 2, 5, 5),
(7, 3, 4, 0.333333), (7, 3, 5, 3),
(7, 4, 5, 4);

-- C8: Kepuasan Auditee (Tabel 4.38)
INSERT INTO perbandingan_alternatif (id_kriteria, id_auditor_1, id_auditor_2, nilai) VALUES
(8, 1, 2, 0.5), (8, 1, 3, 4), (8, 1, 4, 2), (8, 1, 5, 5),
(8, 2, 3, 5), (8, 2, 4, 3), (8, 2, 5, 6),
(8, 3, 4, 0.5), (8, 3, 5, 3),
(8, 4, 5, 4);
