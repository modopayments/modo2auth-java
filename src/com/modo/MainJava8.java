package com.modo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainJava8 {

    private static final String AUTH_HEADER = "Authorization";

    public static void main(String[] args) throws IOException {
        String apiHostUrl = "MODO_HOST_URL_HERE";
        String apiKey = "API_KEY_HERE";
        String apiSecret = "API_SECRET_HERE";

        Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);

        MainJava8.exerciseModo2ApiPost(auth, apiHostUrl);
        MainJava8.exerciseModo2ApiGet(auth, apiHostUrl);

    }

    private static void exerciseModo2ApiPost(Modo2Auth auth, String apiHostUrl) throws IOException {
        String postApiUri = "/v2/reports";
        String requestBody = "{\"start_date\": \"2020-07-13T00:00:00Z\",\"end_date\": \"2020-07-13T23:59:59Z\"}";
        byte[] bodyOut = requestBody.getBytes(StandardCharsets.US_ASCII);
        int bodyLength = bodyOut.length;

        String authToken = auth.createModoToken(postApiUri, requestBody);

        URL url = new URL(apiHostUrl + postApiUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty(AUTH_HEADER, authToken);
        connection.setFixedLengthStreamingMode(bodyLength);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        connection.connect();

        try (OutputStream os = connection.getOutputStream()) {
            os.write(bodyOut);
        }

        String responseBody;
        try (InputStream is = connection.getInputStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            responseBody = result.toString("UTF-8");
        }

        int responseCode = connection.getResponseCode();

        if (HttpURLConnection.HTTP_OK == responseCode) {
            System.out.println("Successful request to URL for POST: " + apiHostUrl);
            System.out.println(responseBody);
        } else if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {
            System.out.println("Unauthorized status code returned from server for POST, please check your URL and credentials");
            System.exit(1);
        } else {
            System.out.println("Unknown error returned from the server for POST. Response Code: " + responseCode);
            System.exit(1);
        }
    }

    private static void exerciseModo2ApiGet(Modo2Auth auth, String apiHostUrl) throws IOException {
        String postApiUri = "/v2/vault/public_key";
        String authToken = auth.createModoToken(postApiUri, "");

        URL url = new URL(apiHostUrl + postApiUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty(AUTH_HEADER, authToken);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(false);

        connection.connect();

        String responseBody;
        try (InputStream is = connection.getInputStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            responseBody = result.toString("UTF-8");
        }

        int responseCode = connection.getResponseCode();

        if (HttpURLConnection.HTTP_OK == responseCode) {
            System.out.println("Successful request to GET URL: " + apiHostUrl);
            System.out.println(responseBody);
        } else if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {
            System.out.println("Unauthorized status code returned from server for GET, please check your URL and credentials");
            System.exit(1);
        } else {
            System.out.println("Unknown error returned from the server for GET. Response Code: " + responseCode);
            System.exit(1);
        }
    }

}
