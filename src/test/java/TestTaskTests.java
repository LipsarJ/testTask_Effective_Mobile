import org.example.TestTaskApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = TestTaskApplication.class)
@ActiveProfiles("test")
@Testcontainers
class TestTaskTests {
    @Test
    void contextLoads() {
    }

}

