ALTER TABLE speaking_records
    ADD COLUMN record_type VARCHAR(30) NOT NULL DEFAULT 'QUESTION_ANALYSIS' AFTER daily_question_id,
    ADD COLUMN prompt_text TEXT NULL AFTER record_type;

ALTER TABLE speaking_records
    MODIFY daily_question_id BIGINT NULL;
