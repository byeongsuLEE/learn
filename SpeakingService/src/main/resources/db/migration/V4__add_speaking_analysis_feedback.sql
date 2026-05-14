ALTER TABLE speaking_analyses
    ADD COLUMN feedback_json JSON NULL AFTER render_blocks_json;
