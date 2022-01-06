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
        //String apiHostUrl = "MODO_HOST_URL_HERE";
        //String apiKey = "API_KEY_HERE";
        //String apiSecret = "API_SECRET_HERE";
        String apiHostUrl = "https://checkout.int-31.modopayments.io";
        String apiKey = "a8BR7AqB-xs2HoIdEi_wtMAkSNXvLAe4";
        String apiSecret = "CAvku68wPkn6aB0hKWKpi-9OOemjwZTkgRIQbhSj37MMikpCjmooXI8gdXfM_1cs";

        String postApiUri = "/v3/checkout/list";
        String requestBody = "{\"checkout_ids\": []}";

        Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);
        String authToken = auth.createModoToken(postApiUri, requestBody);
        System.out.println("MODO2 auth token: " + authToken);

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

        String getApiUri = "/v3/vault/modo_public_key";
        authToken = auth.createModoToken(getApiUri, "");
        System.out.println("MODO2 auth token: " + authToken);

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
