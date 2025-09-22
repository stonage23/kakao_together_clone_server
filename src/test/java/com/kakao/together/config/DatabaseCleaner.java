package com.kakao.together.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Profile("test")
@Slf4j
public class DatabaseCleaner {

    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    // 바뀐부분 : 의존성 주입이후 초기화 수행 시 Table을 조회한다.
    @PostConstruct
    @SuppressWarnings("unchecked")
    private void findDatabaseTableNames() {
        List<Object[]> tableInfos = entityManager.createNativeQuery("SHOW TABLES").getResultList();

        Iterator iter = tableInfos.iterator();
        while (iter.hasNext()) {
            Object tableInfo = (Object) iter.next();
            String tableName = (String) tableInfo;
            tableNames.add(tableName);
        }
    }

    private void truncate() {
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = %d", 0)).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format("TRUNCATE TABLE %s", tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = %d", 1)).executeUpdate();
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }
}
