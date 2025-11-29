package userservice;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @BeforeAll
    static void beforeAll() {
    }

    public static String getJdbcUrl() {
        return postgres.getJdbcUrl();
    }

    public static String getUsername() {
        return postgres.getUsername();
    }

    public static String getPassword() {
        return postgres.getPassword();
    }
}