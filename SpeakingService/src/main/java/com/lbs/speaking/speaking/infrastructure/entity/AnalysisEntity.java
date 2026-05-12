package com.lbs.speaking.speaking.infrastructure.entity;

import com.lbs.speaking.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "speaking_analyses",
        indexes = @Index(name = "idx_speaking_analysis_record", columnList = "record_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private SpeakingRecordEntity record;

    @Column(name = "improved_text", nullable = false, columnDefinition = "TEXT")
    private String improvedText;

    @Column(name = "issues_json", nullable = false, columnDefinition = "JSON")
    private String issuesJson;

    @Column(name = "render_blocks_json", nullable = false, columnDefinition = "JSON")
    private String renderBlocksJson;

    private AnalysisEntity(SpeakingRecordEntity record, String improvedText, String issuesJson, String renderBlocksJson) {
        this.record = record;
        this.improvedText = improvedText;
        this.issuesJson = issuesJson;
        this.renderBlocksJson = renderBlocksJson;
    }

    public static AnalysisEntity create(SpeakingRecordEntity record, String improvedText, String issuesJson,
                                        String renderBlocksJson) {
        return new AnalysisEntity(record, improvedText, issuesJson, renderBlocksJson);
    }

    public void replace(String improvedText, String issuesJson, String renderBlocksJson) {
        this.improvedText = improvedText;
        this.issuesJson = issuesJson;
        this.renderBlocksJson = renderBlocksJson;
    }
}
