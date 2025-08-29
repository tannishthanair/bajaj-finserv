import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;

@Component
public class JavaTaskRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting the task...");

        // Step 1: Generate Webhook
        String regNo = "REG12347"; // Use your actual registration number
        JsonNode generateWebhookResponse = generateWebhook(regNo);

        // Corrected line: Assumes the actual JSON key is "webhookUrl"
        String webhookUrl = generateWebhookResponse.get("webhookUrl").asText();
        String accessToken = generateWebhookResponse.get("accessToken").asText();

        // Step 2: Solve the SQL Problem
        String finalQuery = solveSqlProblem(regNo);

        // Step 3: Submit the Solution
        submitSolution(webhookUrl, accessToken, finalQuery);
    }

    private JsonNode generateWebhook(String regNo) {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String
                .format("{\"name\": \"John Doe\", \"regNo\": \"%s\", \"email\": \"john@example.com\"}", regNo);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        System.out.println("Sending POST request to generate webhook...");
        return restTemplate.postForObject(url, entity, JsonNode.class);
    }

    private String solveSqlProblem(String regNo) {
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));
        String problemLink;
        if (lastTwoDigits % 2 != 0) { // Odd
            problemLink = "https://drive.google.com/file/d/1IeSI616KoSQ_AF_f_RihIT9tEDICtoz-G/view?usp=sharing";
            System.out.println("RegNo is odd, proceeding to Question 1: " + problemLink);
            return "SELECT * FROM odd_problem_table WHERE some_condition;";
        } else { // Even
            problemLink = "https://drive.google.com/file/d/143MR5cLFrINEuHzzWJ5RHnEWuijuM9X/view?usp=sharing";
            System.out.println("RegNo is even, proceeding to Question 2: " + problemLink);
            return "SELECT COUNT(*) FROM even_problem_table WHERE another_condition;";
        }
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"finalQuery\": \"%s\"}", finalQuery);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        System.out.println("Submitting the final query...");
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
        System.out.println("Solution submitted successfully!");
    }
}