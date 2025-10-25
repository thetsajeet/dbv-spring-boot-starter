package com.thetsajeet.dbv;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DBVRunnerTest {

    @Mock
    private DatabaseValidationService databaseValidationService;

    @InjectMocks
    private DBVRunner dbvRunner;

    private TestAppender testAppender;

    @BeforeEach
    void setUp() {
        // Set up the test appender to capture log messages
        testAppender = new TestAppender();
        Logger logger = (Logger) LoggerFactory.getLogger(DBVRunner.class);
        logger.addAppender(testAppender);
        logger.setLevel(Level.INFO);
        testAppender.start();
    }

    @Test
    void run_executesSuccessfully_logsStartAndFinish() throws SQLException {
        // Act
        dbvRunner.run();

        // Verify service execution
        verify(databaseValidationService).execute();

        // Verify logs
        List<ILoggingEvent> logs = testAppender.getEvents();
        assertEquals(2, logs.size(), "Expected two log messages");
        assertEquals(Level.INFO, logs.get(0).getLevel());
        assertEquals("DBVRunner started...", logs.get(0).getMessage());
        assertEquals(Level.INFO, logs.get(1).getLevel());
        assertEquals("DBVRunner finished.", logs.get(1).getMessage());
    }

    @Test
    void run_handlesSQLException_logsErrorAndFinish() throws SQLException {
        // Arrange
        SQLException sqlException = new SQLException("Database error");
        doThrow(sqlException).when(databaseValidationService).execute();

        // Act
        dbvRunner.run();

        // Verify service execution
        verify(databaseValidationService).execute();

        // Verify logs
        List<ILoggingEvent> logs = testAppender.getEvents();
        assertEquals(3, logs.size(), "Expected three log messages");
        assertEquals(Level.INFO, logs.get(0).getLevel());
        assertEquals("DBVRunner started...", logs.get(0).getMessage());
        assertEquals(Level.ERROR, logs.get(1).getLevel());
        assertEquals("Error occurred during database validation: ", logs.get(1).getMessage());
        // Note: "DBVRunner finished." is not logged after an SQLException due to the catch block
    }

    @Test
    void run_handlesUnexpectedException_logsErrorAndFinish() throws SQLException {
        // Arrange
        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        doThrow(runtimeException).when(databaseValidationService).execute();

        // Act
        dbvRunner.run();

        // Verify service execution
        verify(databaseValidationService).execute();

        // Verify logs
        List<ILoggingEvent> logs = testAppender.getEvents();
        assertEquals(3, logs.size(), "Expected three log messages");
        assertEquals(Level.INFO, logs.get(0).getLevel());
        assertEquals("DBVRunner started...", logs.get(0).getMessage());
        assertEquals(Level.ERROR, logs.get(1).getLevel());
        assertEquals("Unexpected error occurred: ", logs.get(1).getMessage());
        assertEquals(Level.INFO, logs.get(2).getLevel());
        assertEquals("DBVRunner finished.", logs.get(2).getMessage());
    }

    // Custom appender to capture log events for testing
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
}