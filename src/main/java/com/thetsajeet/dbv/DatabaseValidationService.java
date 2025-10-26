package com.thetsajeet.dbv;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SequenceGenerator;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
@AllArgsConstructor
class DatabaseValidationService {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public void execute() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            log.info("Database connection successful");
            DatabaseMetaData metaData = connection.getMetaData();

            log.info("Scanning all available entities...");
            for(EntityType<?> entity : entityManagerFactory.getMetamodel().getEntities()) {

                Class<?> javaType = entity.getJavaType();
                String tableName = getTableNameFromEntity(javaType);

                try (ResultSet tables = metaData.getTables(null, connection.getSchema(), tableName.toLowerCase(), new String[]{"TABLE", "VIEW"})) {
                    if(!tables.next()) {
                        log.warn("Error: For entity: {}, table {} does not exist in the database!", javaType.getSimpleName(), tableName);
                        continue;
                    }
                    log.info("Table {} exists in the database.", tableName);

                    validateColumns(entity, tableName, connection, metaData);

                    String sequenceName = getSequenceNameFromEntity(javaType);

                    if (sequenceName == null || sequenceName.isEmpty()) {
                        log.info("No sequence found on {}. Skipping sequence validator...", javaType);
                        continue;
                    }

                    if (checkSequenceExists(connection, sequenceName)) {
                        log.info("Sequence {} exists for entity {}", sequenceName, javaType.getSimpleName());
                    } else {
                        log.error("Missing sequence {} for entity {}", sequenceName, javaType.getSimpleName());
                    }
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

    private String getSequenceNameFromEntity(Class<?> entityClass) {
        // Check for @SequenceGenerator annotation
        if (entityClass.isAnnotationPresent(SequenceGenerator.class)) {
            SequenceGenerator sequenceGenerator = entityClass.getAnnotation(SequenceGenerator.class);
            if(!sequenceGenerator.sequenceName().isEmpty()) {
                return sequenceGenerator.sequenceName();
            }
            if (!sequenceGenerator.name().isEmpty()) {
                return sequenceGenerator.name();
            }
        }
        return null;
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

    private String getSequenceCheckQuery(String dbProduct) {
        if (dbProduct.contains("postgresql") || dbProduct.contains("h2") || dbProduct.contains("mysql")) {
            return "SELECT sequence_name FROM information_schema.sequences WHERE sequence_name = ?";
        } else if (dbProduct.contains("oracle")) {
            return "SELECT sequence_name FROM all_sequences WHERE sequence_name = ?";
        } else if (dbProduct.contains("sql server")) {
            return "SELECT name AS sequence_name FROM sys.sequences WHERE name = ?";
        } else {
            throw new UnsupportedOperationException("Unknown DB dialect for sequence validation: " + dbProduct);
        }
    }

    private boolean checkSequenceExists(Connection connection, String sequenceName) {
        try {
            String dbProduct = connection.getMetaData().getDatabaseProductName().toLowerCase();
            String sql = getSequenceCheckQuery(dbProduct);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, sequenceName);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            log.error("Error checking sequence '{}': {}", sequenceName, e.getMessage());
            return false;
        }
    }
}
