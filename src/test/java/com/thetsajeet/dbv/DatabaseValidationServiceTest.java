package com.thetsajeet.dbv;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseValidationServiceTest {
    @Mock
    private DataSource dataSource;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData metaData;

    @Mock
    private Metamodel metamodel;

    @Mock
    private EntityType<TestEntity> entityType;

    @InjectMocks
    private DatabaseValidationService databaseValidationService;

    private TestAppender testAppender;

    @BeforeEach
    void setUp() throws SQLException {
        // Set up the test appender to capture log messages
        testAppender = new TestAppender();
        Logger logger = (Logger) LoggerFactory.getLogger(DatabaseValidationService.class);
        logger.addAppender(testAppender);
        logger.setLevel(Level.INFO);
        testAppender.start();

        // Common setup for connection and metadata
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(connection.getSchema()).thenReturn("public");
        when(entityManagerFactory.getMetamodel()).thenReturn(metamodel);
    }

    @Test
    void execute_successfulConnectionAndTableExists_logsCorrectly() throws SQLException, NoSuchFieldException {
        // Arrange
        when(entityType.getJavaType()).thenReturn(TestEntity.class);
        when(metamodel.entity(TestEntity.class)).thenReturn(entityType);
        when(entityType.getName()).thenReturn("TestEntity");
        when(metamodel.getEntities()).thenReturn(Set.of(entityType));

        ResultSet tableResultSet = mock(ResultSet.class);
//        when(metaData.getTables(null, "public", "test_entity", new String[]{"TABLE", "VIEW"})).thenReturn(tableResultSet);
        when(metaData.getTables(isNull(), anyString(), anyString(), any(String[].class))).thenReturn(tableResultSet);
        when(tableResultSet.next()).thenReturn(true);

        ResultSet columnResultSet = mock(ResultSet.class);
//        when(metaData.getColumns(null, "public", "test_entity", "name")).thenReturn(columnResultSet);
        when(metaData.getColumns(isNull(), anyString(), anyString(), anyString())).thenReturn(columnResultSet);
        when(columnResultSet.next()).thenReturn(true);

        // Act
        databaseValidationService.execute();

        // Verify
        verify(dataSource).getConnection();
        verify(connection).getMetaData();
        verify(entityManagerFactory, times(2)).getMetamodel();
        verify(metaData, times(1)).getTables(isNull(), anyString(), anyString(), any(String[].class));
        verify(metaData, times(2)).getColumns(isNull(), anyString(), anyString(), anyString());

        // Verify logs
        List<ILoggingEvent> logs = testAppender.getEvents();
        assertEquals(6, logs.size(), "Expected four log messages");
        assertEquals(Level.INFO, logs.get(0).getLevel());
        assertEquals("Database connection successful", logs.get(0).getMessage());
        assertEquals(Level.INFO, logs.get(1).getLevel());
        assertEquals(Level.INFO, logs.get(2).getLevel());
        assertEquals(Level.INFO, logs.get(3).getLevel());
    }


    // Helper method to access private getTableNameFromEntity
    private String invokeGetTableNameFromEntity(Class<?> entityClass) throws NoSuchFieldException {
        try {
            java.lang.reflect.Method method = DatabaseValidationService.class.getDeclaredMethod("getTableNameFromEntity", Class.class);
            method.setAccessible(true);
            return (String) method.invoke(databaseValidationService, entityClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getTableNameFromEntity", e);
        }
    }

    // Test entity classes
    static class TestEntity {
        private Long id;
        private String name;
    }

    @Table(name = "custom_table")
    static class TestEntityWithTableAnnotation {
        private String name;
    }

    static class TestEntityWithStaticField {
        private static String staticField;
    }

    // Custom appender to capture log events
    static class TestAppender extends AppenderBase<ILoggingEvent> {
        private final List<ILoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            events.add(eventObject);
        }

        List<ILoggingEvent> getEvents() {
            return events;
        }
    }
/*
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private Metamodel metamodel;
    @Mock
    private EntityType<?> entityType;
    @Mock
    private DatabaseMetaData metaData;
    @Mock
    private ResultSet tablesResultSet;
    @Mock
    private ResultSet columnsResultSet;

    @InjectMocks
    private DatabaseValidationService databaseValidationService;



    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(connection.getSchema()).thenReturn("public");

        // Mock entity metadata
        when(entityManagerFactory.getMetamodel()).thenReturn(metamodel);
        Set<EntityType<?>> entities = new HashSet<>();
        entities.add(entityType);
        when(metamodel.getEntities()).thenReturn(entities);

        // Mock entity type
//        when(entityType.getJavaType()).thenReturn(DummyEntity.class);
        when(entityType.getName()).thenReturn("DummyEntity");

        // Mock getTables result
        when(metaData.getTables(any(), any(), eq("dummy_table"), any()))
                .thenReturn(tablesResultSet);
        when(tablesResultSet.next()).thenReturn(true);

        // Mock getColumns result
        when(metaData.getColumns(any(), any(), eq("dummy_table"), any()))
                .thenReturn(columnsResultSet);
        when(columnsResultSet.next()).thenReturn(true);
    }

    @Test
    void testExecute_WhenTableAndColumnsExist_ShouldPass() throws Exception {
        databaseValidationService.execute();

        // Verify database interactions
        verify(metaData).getTables(null, "public", "dummy_table", new String[]{"TABLE", "VIEW"});
        verify(metaData, atLeastOnce()).getColumns(null, "public", "dummy_table", "name");
        verify(metaData, atLeastOnce()).getColumns(null, "public", "dummy_table", "age");
    }

    // Dummy entity class for testing
    @Entity
    @Table(name = "dummy_table")
    static class DummyEntity {
        @Column(name = "name")
        private String name;
        @Column(name = "age")
        private int age;
    }*/

}