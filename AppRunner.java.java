package com.yourname.bajajchallenge;

import com.yourname.bajajchallenge.dto.SolutionRequest;
import com.yourname.bajajchallenge.dto.WebhookRequest;
import com.yourname.bajajchallenge.dto.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application started, beginning the task...");

        RestTemplate restTemplate = new RestTemplate();

        // === Step 1: Generate Webhook ===
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        WebhookRequest webhookRequest = new WebhookRequest();
        webhookRequest.setName("YOUR NAME"); // <-- Fill this in
        webhookRequest.setRegNo("YOUR REG NO");   // <-- Fill this in
        webhookRequest.setEmail("YOUR EMAIL"); // <-- Fill this in

        WebhookResponse webhookResponse = restTemplate.postForObject(generateUrl, webhookRequest, WebhookResponse.class);

        String receivedWebhookUrl = webhookResponse.getWebhookURL();
        String accessToken = webhookResponse.getAccessToken();

        System.out.println("Received Webhook URL: " + receivedWebhookUrl);
        System.out.println("Received Access Token: " + accessToken);

        // === Step 2: Determine and store SQL query ===
        String regNo = "YOUR REG NO"; // <-- Use the same reg number
        int lastTwoDigits = Integer.parseInt(regNo.replaceAll("\\D", "")) % 100;

        String myFinalQuery;
        if (lastTwoDigits % 2 != 0) { // Odd number
            System.out.println("RegNo ends in odd number. Using Question 1.");
            myFinalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
        } else { // Even number
            System.out.println("RegNo ends in even number. Using Question 2.");
            myFinalQuery = "SQL_QUERY_FOR_QUESTION_2"; // You would need to solve this
        }

        // === Step 3: Submit the Solution ===
        String submissionUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

        SolutionRequest solutionRequest = new SolutionRequest();
        solutionRequest.setFinalQuery(myFinalQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
        String submissionResponse = restTemplate.postForObject(submissionUrl, entity, String.class);

        System.out.println("Submission Response: " + submissionResponse);
    }
}s