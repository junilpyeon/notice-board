CREATE TABLE IF NOT EXISTS notice (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    start_date_time TIMESTAMP,
    end_date_time TIMESTAMP,
    created_date TIMESTAMP,
    view_count INT,
    author VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS notice_attachments (
                                                  notice_id BIGINT NOT NULL,
                                                  attachment_path VARCHAR(255),
    PRIMARY KEY (notice_id, attachment_path),
    FOREIGN KEY (notice_id) REFERENCES notice(id)
    );