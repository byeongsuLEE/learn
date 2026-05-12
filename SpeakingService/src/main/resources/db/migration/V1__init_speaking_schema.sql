CREATE TABLE speaking_question_categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE speaking_questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NULL,
    content VARCHAR(500) NOT NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_speaking_question_category
        FOREIGN KEY (category_id)
        REFERENCES speaking_question_categories (id)
);

CREATE TABLE speaking_daily_questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    question_date DATE NOT NULL,
    question_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_speaking_daily_question_date UNIQUE (question_date),
    CONSTRAINT fk_speaking_daily_question_question
        FOREIGN KEY (question_id)
        REFERENCES speaking_questions (id)
);

CREATE INDEX idx_speaking_daily_question_date
    ON speaking_daily_questions (question_date);

CREATE TABLE speaking_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    daily_question_id BIGINT NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    original_text TEXT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    duration_sec INT NOT NULL,
    status VARCHAR(40) NOT NULL,
    failure_reason VARCHAR(1000) NULL,
    deleted_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_speaking_record_user_daily_question UNIQUE (user_id, daily_question_id),
    CONSTRAINT fk_speaking_record_daily_question
        FOREIGN KEY (daily_question_id)
        REFERENCES speaking_daily_questions (id)
);

CREATE INDEX idx_speaking_record_user_created
    ON speaking_records (user_id, created_at);

CREATE INDEX idx_speaking_record_status_updated_at
    ON speaking_records (status, updated_at);

CREATE TABLE speaking_analyses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    record_id BIGINT NOT NULL,
    improved_text TEXT NOT NULL,
    issues_json JSON NOT NULL,
    render_blocks_json JSON NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_speaking_analysis_record UNIQUE (record_id),
    CONSTRAINT fk_speaking_analysis_record
        FOREIGN KEY (record_id)
        REFERENCES speaking_records (id)
);

CREATE INDEX idx_speaking_analysis_record
    ON speaking_analyses (record_id);

INSERT INTO speaking_question_categories (name, created_at, updated_at)
VALUES
    ('Daily Life', NOW(6), NOW(6)),
    ('Self Reflection', NOW(6), NOW(6)),
    ('Work and Study', NOW(6), NOW(6));

INSERT INTO speaking_questions (category_id, content, active, created_at, updated_at)
VALUES
    (1, 'What habit helped you grow the most this year?', 1, NOW(6), NOW(6)),
    (1, 'Describe a small routine that makes your day better.', 1, NOW(6), NOW(6)),
    (2, 'What is one mistake that taught you something useful?', 1, NOW(6), NOW(6)),
    (2, 'What kind of person do you want to become this year?', 1, NOW(6), NOW(6)),
    (3, 'Tell me about a skill you are currently trying to improve.', 1, NOW(6), NOW(6)),
    (3, 'How do you stay focused when a task is difficult?', 1, NOW(6), NOW(6));
