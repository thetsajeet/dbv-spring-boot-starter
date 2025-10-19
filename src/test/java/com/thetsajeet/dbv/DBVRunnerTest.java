package com.thetsajeet.dbv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBVRunnerTest {
    @Mock
    private DBVRunner dbvRunner;

    @InjectMocks
    private DatabaseValidationService databaseValidationService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testDBVRunner_doesNotThrowException()  {
        // Arrange

        // ACT
        assertDoesNotThrow(() -> dbvRunner.run());
    }

    @Test
    void testDBVRunner_doesThrowException()  {
        // Arrange
        doThrow(new SQLException("Database error"))
                .when(dbvRunner)
                .run();

        // Act
        assertThrows(SQLException.class, () -> dbvRunner.run());
    }
}