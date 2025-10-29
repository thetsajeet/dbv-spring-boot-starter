package com.thetsajeet.dbv;

import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

@Slf4j
@AllArgsConstructor
public class DBVRunner implements CommandLineRunner {

  private final DatabaseValidationService databaseValidationService;

  @Override
  public void run(String... args) {
    try {
      log.info("DBVRunner started...");
      databaseValidationService.execute();
    } catch (SQLException e) {
      log.error("Error occurred during database validation: ", e);
    } catch (Exception e) {
      log.error("Unexpected error occurred: ", e);
    } finally {
      log.info("DBVRunner finished.");
    }
  }
}
