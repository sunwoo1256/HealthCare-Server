package cerberus.HealthCare.openAI;

import cerberus.HealthCare.openAI.ChatGPT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChatGPTTest {

    private ChatGPT chat;

    @Value("${openai.api-key}")
    private String API_KEY;

    @BeforeEach
    public void setUp() {
        chat = new ChatGPT(API_KEY);
    }

    @Test
    public void testGetCompletionMessageBlocking() {
        // Given
        String prompt = "say '안녕하세요'";

        // When
        String result = chat.getCompletionMessageBlocking(prompt);

        // Then
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        System.out.println("Response: " + result);

        // Optional: Check if response contains expected text
        assertTrue(result.toLowerCase().contains("안녕하세요"),
                "Response should contain the word 'test'");
    }
}