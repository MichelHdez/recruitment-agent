CREATE TABLE IF NOT EXISTS vacancy (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  description TEXT,
  requirements TEXT,
  experience_years INT, -- <--- Agregado aquí
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cv (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  candidate_name VARCHAR(255),
  file_name VARCHAR(255),
  extracted_text TEXT,
  skills TEXT,
  experience_years INT,
  education TEXT,
  status VARCHAR(50),
  vacancy_id BIGINT,
  processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (vacancy_id) REFERENCES vacancy(id)
);

CREATE TABLE IF NOT EXISTS metrics (
  id INT PRIMARY KEY,
  cvs_processed INT,
  candidates_aptos INT,
  candidates_no_aptos INT,
  candidates_revision_manual INT
);
INSERT IGNORE INTO metrics (id, cvs_processed, candidates_aptos, candidates_no_aptos, candidates_revision_manual) VALUES (1,0,0,0,0);

ALTER TABLE vacancy MODIFY description TEXT;
ALTER TABLE vacancy MODIFY requirements TEXT;
