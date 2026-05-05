package com.lbs.user.card.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Profile("seed")
@RequiredArgsConstructor
@Slf4j
public class DeckSeedRunner implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        String[] categories = {"수학", "영어", "과학", "국어", "사회"};
        int batchSize = 1000;
        int perCategory = 20_000;

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sql = "INSERT INTO decks (title, description, category, tag, card_count, created_by, last_modified_by, created_date, last_modified_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (String cat : categories) {
            List<Object[]> batch = new ArrayList<>();
            for (int i = 0; i < perCategory; i++) {
                String title = "BENCH_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                batch.add(new Object[]{title, "벤치마크 테스트 데이터 " + i, cat, "bench", 0, "seed", "seed", now, now});
                if (batch.size() == batchSize) {
                    jdbcTemplate.batchUpdate(sql, batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) jdbcTemplate.batchUpdate(sql, batch);
            log.info("시드 완료: {} - {}건", cat, perCategory);
        }
        log.info("전체 시드 완료: 100,000건");
    }
}
