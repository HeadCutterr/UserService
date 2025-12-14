package com.example.notificationservice;

import com.example.notificationservice.event.UserEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class NotificationServiceIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Container
    static final GenericContainer<?> mailhog = new GenericContainer<>(
            DockerImageName.parse("mailhog/mailhog:latest"))
            .withExposedPorts(8025, 1025);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        registry.add("spring.mail.properties.mail.smtp.auth", () -> false);
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> false);
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testHandleUserEvent_Create() {
        String testEmail = "newuser@example.com";
        UserEvent event = new UserEvent(testEmail, UserEvent.EventType.CREATE);

        kafkaTemplate.send("user-events", event);

        await().atMost(java.time.Duration.ofSeconds(20))
                .pollInterval(java.time.Duration.ofMillis(500))
                .untilAsserted(() -> {
                    List<MailHogMessage> messages = MailHogClient.getMessages(mailhog.getHost(), mailhog.getMappedPort(8025));
                    System.out.println("Checking for CREATE email. Found messages: " + messages.size());

                    assert !messages.isEmpty() : "No emails received within timeout";
                    assert messages.get(0).getContent().getHeaders().get("To").contains(testEmail) : "Email To header does not match. Expected: " + testEmail + ", Got: " + messages.get(0).getContent().getHeaders().get("To");

                    List<String> subjects = messages.get(0).getContent().getHeaders().get("Subject");
                    assert subjects != null && !subjects.isEmpty() : "Email Subject header is missing or empty";

                    boolean subjectMatched = subjects.stream()
                            .anyMatch(subject -> subject.contains("0JfQtNGA0LDQstGB0YLQstGD0LnRgtC1ISDQktCw0Ygg"));
                    assert subjectMatched : "Email Subject does not contain expected Base64 fragment for CREATE event. Got subjects: " + subjects;
                });
    }

    @Test
    void testHandleUserEvent_Delete() {
        String testEmail = "olduser@example.com";
        UserEvent event = new UserEvent(testEmail, UserEvent.EventType.DELETE);

        kafkaTemplate.send("user-events", event);

        await().atMost(java.time.Duration.ofSeconds(20))
                .pollInterval(java.time.Duration.ofMillis(500))
                .untilAsserted(() -> {
                    List<MailHogMessage> messages = MailHogClient.getMessages(mailhog.getHost(), mailhog.getMappedPort(8025));
                    System.out.println("Checking for DELETE email. Found messages: " + messages.size());

                    assert !messages.isEmpty() : "No emails received within timeout";
                    assert messages.get(0).getContent().getHeaders().get("To").contains(testEmail) : "Email To header does not match. Expected: " + testEmail + ", Got: " + messages.get(0).getContent().getHeaders().get("To");

                    List<String> subjects = messages.get(0).getContent().getHeaders().get("Subject");
                    assert subjects != null && !subjects.isEmpty() : "Email Subject header is missing or empty";

                    boolean subjectMatched = subjects.stream()
                            .anyMatch(subject -> subject.contains("0JfQtNGA0LDQstGB0YLQstGD0LnRgtC1ISDQvdCwINGB0LA="));
                    assert subjectMatched : "Email Subject does not contain expected Base64 fragment for DELETE event. Got subjects: " + subjects;
                });
    }
}