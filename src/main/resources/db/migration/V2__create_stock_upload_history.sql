CREATE TABLE stock_upload_history (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT REFERENCES fornecedor(id),
    file_name VARCHAR(255) NOT NULL,
    upload_source VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_lines_processed INT DEFAULT 0,
    valid_lines INT DEFAULT 0,
    invalid_lines INT DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP
);
