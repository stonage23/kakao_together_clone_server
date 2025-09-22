package com.kakao.together.domain;

import com.kakao.together.config.JpaConfig;
import com.kakao.together.domain.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(JpaConfig.class)
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BaseTimeEntityAuditingTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    static class AuditingTestEntity extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String text;
    }

    @Test
    @DisplayName("엔티티 생성 및 업데이트에 Auditing 적용 테스트")
    public void testAuditing() {
        // Given
        AuditingTestEntity entity = new AuditingTestEntity();
        entity.setText("엔티티생성");
        entityManager.persist(entity);
        entityManager.flush();
        // When
        AuditingTestEntity savedEntity = entityManager.find(AuditingTestEntity.class, entity.getId());
        // Then
        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isEqualTo(savedEntity.getCreatedAt());
        log.info(savedEntity.getCreatedAt().toString());
        log.info(savedEntity.getUpdatedAt().toString());
        // When
        savedEntity.setText("엔티티업데이트");
        entityManager.persist(savedEntity);
        entityManager.flush();
        AuditingTestEntity updatedEntity = entityManager.find(AuditingTestEntity.class, savedEntity.getId());
        // Then
        assertThat(updatedEntity.getUpdatedAt()).isAfter(updatedEntity.getCreatedAt());
        log.info(savedEntity.getUpdatedAt().toString());
    }
}