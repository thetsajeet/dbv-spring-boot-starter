package com.thetsajeet.dbv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

@Slf4j
@AllArgsConstructor
public class DBVRunner implements CommandLineRunner {

    private final DatabaseValidationService databaseValidationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("DBVRunner started...");
        databaseValidationService.execute();
        log.info("DBVRunner finished.");
    }
}
