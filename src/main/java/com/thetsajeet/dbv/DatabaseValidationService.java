package com.thetsajeet.dbv;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
class DatabaseValidationService {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final DbvStarterProperties properties;

    public void execute() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            log.info("Database connection successful");
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("Scanning all available entities...");

            for(EntityType<?> entity : entityManagerFactory.getMetamodel().getEntities()) {
                Class<?> javaType = entity.getJavaType();
                String tableName = getTableNameFromEntity(javaType);

                // Build table types based on configuration
                List<String> tableTypes = new ArrayList<>();
                if (properties.isVerifyTables()) {
                    tableTypes.add("TABLE");
                }
                if (properties.isVerifyViews()) {
                    tableTypes.add("VIEW");
                }

                // Skip validation if no types are enabled
                if (tableTypes.isEmpty()) {
                    log.info("Table and view verification are both disabled. Skipping validation.");
                    return;
                }

                try (ResultSet tables = metaData.getTables(null, connection.getSchema(), tableName.toLowerCase(), tableTypes.toArray(new String[0]))) {
                    if(!tables.next()) {
                        log.warn("Error: For entity: {}, table {} does not exist in the database!", javaType.getSimpleName(), tableName);
                        continue;
                    }
                    log.info("Table {} exists in the database.", tableName);
                    validateColumns(entity, tableName, connection, metaData);
                }
            }
        }
    }

    private String getColumnNameFromField(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        return columnAnnotation != null && !columnAnnotation.name().isEmpty() ? columnAnnotation.name() : field.getName();
    }

    private String getTableNameFromEntity(Class<?> entityClass) {
        // Check for @Table annotation
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if (!tableAnnotation.name().isEmpty()) {
                return tableAnnotation.name();
            }
        }
        return entityManagerFactory.getMetamodel().entity(entityClass).getName();
    }

    private void validateColumns(EntityType<?> entity, String tableName, Connection conn, DatabaseMetaData metaData) throws SQLException {
        log.info("Scanning all columns in {}", entity.getName());
        for (Field field : entity.getJavaType().getDeclaredFields()) {
            // ignore static fields
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            String columnName = getColumnNameFromField(field);
            try (ResultSet rs = metaData.getColumns(null, conn.getSchema(), tableName, columnName)) {
                if(!rs.next()) {
                    log.info("Missing column: {}", columnName);
                    continue;
                }
                log.info("Column found: {}", columnName);
            }
        }
    }
}
