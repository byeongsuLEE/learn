ALTER TABLE speaking_daily_questions
    ADD COLUMN question_type VARCHAR(30) NOT NULL DEFAULT 'DAILY' AFTER question_date;

ALTER TABLE speaking_daily_questions
    DROP INDEX uk_speaking_daily_question_date;

ALTER TABLE speaking_daily_questions
    ADD CONSTRAINT uk_speaking_daily_question_date_type UNIQUE (question_date, question_type);

INSERT INTO speaking_question_categories (name, created_at, updated_at)
SELECT 'Interview', NOW(6), NOW(6)
WHERE NOT EXISTS (
    SELECT 1
    FROM speaking_question_categories
    WHERE name = 'Interview'
);

INSERT INTO speaking_questions (category_id, content, active, created_at, updated_at)
SELECT c.id, 'What is one small win you had recently, and why did it matter to you?', 1, NOW(6), NOW(6)
FROM speaking_question_categories c
WHERE c.name = 'Daily Life'
  AND NOT EXISTS (
      SELECT 1
      FROM speaking_questions
      WHERE content = 'What is one small win you had recently, and why did it matter to you?'
  );

INSERT INTO speaking_questions (category_id, content, active, created_at, updated_at)
SELECT c.id, q.content, 1, NOW(6), NOW(6)
FROM speaking_question_categories c
JOIN (
    SELECT 'Tell me about yourself and your background.' AS content
    UNION ALL
    SELECT 'Why are you interested in this position?'
    UNION ALL
    SELECT 'Describe a challenging project you worked on and how you handled it.'
    UNION ALL
    SELECT 'Tell me about a time you received difficult feedback.'
    UNION ALL
    SELECT 'What are your strengths, and how do they help your team?'
    UNION ALL
    SELECT 'Where do you want to grow professionally over the next year?'
) q
WHERE c.name = 'Interview'
  AND NOT EXISTS (
      SELECT 1
      FROM speaking_questions
      WHERE content = q.content
  );
