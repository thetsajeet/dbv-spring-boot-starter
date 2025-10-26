package com.thetsajeet.dbv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class DatabaseValidationServiceTest {
    @Autowired
    private DatabaseValidationService databaseValidationService;

    @Test
    void testGetTableNameFromEntity() {}
}