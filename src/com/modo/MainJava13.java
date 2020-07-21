package com.modo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MainJava13 {

    private static final String AUTH_HEADER = "Authorization";

    public static void main(String[] args) throws IOException, InterruptedException {
        String apiHostUrl = "MODO_HOST_URL_HERE";
        String apiKey = "API_KEY_HERE";
        String apiSecret = "API_SECRET_HERE";

        String postApiUri = "/v2/reports";
        String requestBody = "{\"start_date\": \"2020-07-13T00:00:00Z\",\"end_date\": \"2020-07-13T23:59:59Z\"}";

        Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);
        String authToken = auth.createModoToken(postApiUri, requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiHostUrl+postApiUri))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(AUTH_HEADER, authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        if (HttpURLConnection.HTTP_OK == postResponse.statusCode()) {
            System.out.println("Successful request to URL for POST: " + apiHostUrl);
            System.out.println(postResponse.body());
        } else if (HttpURLConnection.HTTP_UNAUTHORIZED == postResponse.statusCode()) {
            System.out.println("Unauthorized status code returned from server for POST, please check your URL and credentials");
            return;
        } else {
            System.out.println("Unknown error returned from the server for POST. Response Code: " + postResponse.statusCode());
            return;
        }


        String getApiUri = "/v2/vault/public_key";
        authToken = auth.createModoToken(getApiUri, "");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiHostUrl+getApiUri))
                .GET()
                .header(AUTH_HEADER, authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        if (HttpURLConnection.HTTP_OK == getResponse.statusCode()) {
            System.out.println("Successful request to URL for GET: " + apiHostUrl);
            System.out.println(getResponse.body());
        } else if (HttpURLConnection.HTTP_UNAUTHORIZED == getResponse.statusCode()) {
            System.out.println("Unauthorized status code returned from server for GET, please check your URL and credentials");
            return;
        } else {
            System.out.println("Unknown error returned from the server for GET. Response Code: " + getResponse.statusCode());
            return;
        }
    }

}
