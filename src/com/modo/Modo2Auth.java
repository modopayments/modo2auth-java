package com.modo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

public class Modo2Auth {

    private static final String MODO_AUTH_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String MODO_MAC_AUTH_ALGORITHM = "HmacSHA256";
    private static final String MODO_HASH_ALGORITHM = "SHA-256";
    private static final Charset MODO_CHARSET = StandardCharsets.US_ASCII;

    // Fields initialized in constructor
    private Mac hmac;
    private MessageDigest shaDigest;
    private String header;
    private String apiId;

    public Modo2Auth(String apiId, String apiSecret) {
        try {
            // Init hash algorithm
            byte[] secretBytes = apiSecret.getBytes(MODO_CHARSET);
            hmac = Mac.getInstance(MODO_MAC_AUTH_ALGORITHM);
            hmac.init(new SecretKeySpec(secretBytes, MODO_MAC_AUTH_ALGORITHM));

            // Init Message Digest for SHA-256 hashing
            shaDigest = MessageDigest.getInstance(MODO_HASH_ALGORITHM);

            // Encode header and apiId for future token creation
            this.header = Base64.getEncoder().encodeToString(MODO_AUTH_HEADER.getBytes(MODO_CHARSET));
            this.apiId = apiId;
        } catch (Exception e) {
            throw new RuntimeException("Error in initialization of Modo2Auth library", e);
        }
    }

    // Create a token without a request body, usually for a GET request
    public String createModoToken(String uri) {
        return createModoToken(uri, "");
    }

    // Create a token, utilizing the request body, usually for a POST, PUT, or DELETE request
    public String createModoToken(String uri, String requestBody) {
        // Null request body is assumed to be empty, i.e. for a GET request
        if (requestBody == null)
            requestBody = "";

        // Generate SHA for request body
        byte[] hashBytes = shaDigest.digest(requestBody.getBytes());
        String bodySha = bytesToHexString(hashBytes).replace("-", "").toLowerCase();

        // Create payload Base64 string
        String currentTime = Long.toString(Instant.now().toEpochMilli()).substring(0, 10);
        String payloadPlain = String.format("{\"iat\":%s,\"api_identifier\":\"%s\",\"api_uri\":\"%s\",\"body_hash\":\"%s\"}", currentTime, apiId, uri, bodySha);
        String payload = Base64.getUrlEncoder().encodeToString(payloadPlain.getBytes(MODO_CHARSET)).replaceAll("=+$", "");

        // Sign payload with secret
        String signature = createSignature(payload);

        // Build and return modo auth token
        return String.format("MODO2 %s.%s.%s", header, payload, signature);

    }

    private String createSignature(String payload) {
        // Hash header and payload
        byte[] message = String.format("%s.%s", header, payload).getBytes(MODO_CHARSET);
        byte[] messageHash = hmac.doFinal(message);

        // Base64 encode combination
        String signature = Base64.getEncoder().encodeToString(messageHash);

        // Replace URL possible characters with stand-ins
        return signature.replace("+", "-").replace("/", "_").replaceAll("=+$", "");
    }

    private static final byte[] HEX_CHARSET = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    
    public static String bytesToHexString(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHARSET[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHARSET[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
