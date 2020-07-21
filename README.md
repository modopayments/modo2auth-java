## modo2auth-java

> A core Java solution to generate authentication tokens to communicate with the Modo servers

### Prerequesites

**Credentials** that are created and shared by Modo. These will be different for each environment (`int`, `prod`, `local` etc...).

- `apiKey` - API key from Modo
- `apiSecret` - API secret from Modo

These values will be used when instantiating the Modo2Auth class.

**API URL** targeting an appropriate Modo environment. 

- `apiHostUrl` - URL of a given Modo environment.

### Example Usage - Java 8
Here's an example using core Java 8 libraries to make a GET and POST request. You can use your preferred method or library.

See this code in action by running the class `./src/com/modo/MainJava8.java` using the Java1.8 JRE. 

#### `POST` Example - Java 8
```
# 1 - Instantiate the Modo2Auth class with a given apiKey and apiSecret
Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);

# 2 - Define the Requst Path and Request body, setup the request body length for transport
String postApiUri = "/v2/reports";
String requestBody = "{\"start_date\": \"2020-07-13T00:00:00Z\",\"end_date\": \"2020-07-13T23:59:59Z\"}";
byte[] bodyOut = requestBody.getBytes(StandardCharsets.US_ASCII);
int bodyLength = bodyOut.length;

# 3 - Generate an auth token for a given endpoint and request body
String authToken = auth.createModoToken(postApiUri, requestBody);

# 4 - Create a connection to the Modo server, setting up the header "Authorization" equal to your auth token
URL url = new URL(apiHostUrl + postApiUri);
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("POST");
connection.setRequestProperty("Content-Type", "application/json");
connection.setRequestProperty("Authorization", authToken);
connection.setFixedLengthStreamingMode(bodyLength);
connection.setUseCaches(false);
connection.setDoInput(true);
connection.setDoOutput(true);
connection.connect();

# 5 - Write the request body to the Connection
try (OutputStream os = connection.getOutputStream()) {
    os.write(bodyOut);
}

# 6 - Read the response body off the Connection
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
```

#### `GET` Example - Java 8
```
# 1 - Instantiate the Modo2Auth class with a given apiKey and apiSecret
Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);

# 2 - Define the GET Requst Path, setup the request body length for transport
String getApiUri = "/v2/vault/public_key";

# 3 - Generate an auth token for a given endpoint, omitting the request body since this is a GET request
String authToken = auth.createModoToken(postApiUri);

# 4 - Create a connection to the Modo server, setting up the header "Authorization" equal to your auth token
URL url = new URL(apiHostUrl + getApiUri);
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("GET");
connection.setRequestProperty("Content-Type", "application/json");
connection.setRequestProperty(AUTH_HEADER, authToken);
connection.setUseCaches(false);
connection.setDoInput(true);
connection.setDoOutput(false);
connection.connect();

# 5 - Read the response body off the Connection
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
```

### Example Usage - Java 13
Here's an example using core Java 13 libraries to make a GET and POST request. You can use your preferred method or library.

See this code in action by running the class `./src/com/modo/MainJava13.java` using the Java1.13 JRE. 

#### `POST` Example - Java 13
```
# 1 - Instantiate the Modo2Auth class with a given apiKey and apiSecret
Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);

# 2 - Define the Requst Path and Request body
String postApiUri = "/v2/reports";
String requestBody = "{\"start_date\": \"2020-07-13T00:00:00Z\",\"end_date\": \"2020-07-13T23:59:59Z\"}";

# 3 - Generate an auth token for a given endpoint and request body
String authToken = auth.createModoToken(postApiUri, requestBody);

# 4 - Create an HTTP Client and POST Request, setting the Authorization header
HttpClient client = HttpClient.newHttpClient();
HttpRequest postRequest = HttpRequest.newBuilder()
        .uri(URI.create(apiHostUrl+postApiUri))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .header("Authorization", authToken)
        .header("Content-Type", "application/json")
        .build();

# 5 - Send/receive the request on the HTTP Client
HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
```

#### `GET` Example - Java 13
```
# 1 - Instantiate the Modo2Auth class with a given apiKey and apiSecret
Modo2Auth auth = new Modo2Auth(apiKey, apiSecret);

# 2 - Define the GET Requst Path, setup the request body length for transport
String getApiUri = "/v2/vault/public_key";

# 3 - Generate an auth token for a given endpoint, omitting the request body since this is a GET request
String authToken = auth.createModoToken(getApiUri);

# 4 -  Create an HTTP Client and GET Request, setting up the Authorization header
HttpClient client = HttpClient.newHttpClient();
HttpRequest getRequest = HttpRequest.newBuilder()
        .uri(URI.create(apiHostUrl+getApiUri))
        .GET()
        .header("Authorization", authToken)
        .header("Content-Type", "application/json")
        .build();

# 5 - Send/receive the request on the HTTP Client
HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
```

## Modo2Auth.java

### `public Modo2Auth(String apiId, String apiSecret)`

The constructor for Modo2Auth takes in the apiKey and apiSecret and sets up the appropriate hashing algorithms to generate a Modo2 Auth Token. 

Note: If there is an issue loading the hashing algorithms, a Runtime Error will be thrown out of this constructor. As of 7/21/2020, this constructor works with:
- Java 1.8 - AdoptOpenJDK - 8.0.262.j9
- Java 1.13 - OpenJDK - 13.0.1

### `public String createModoToken(String uri, String requestBody)`

Returns a Modo2 Auth Token string for a request that has a body (POST, PUT, etc.). If request body is null, then token generation will proceed as if it is a GET request.

No exceptions expected during the execution of this function

- `uri` (string) - The request path targeted by this request
- `requestBody` (string) - The exact request body to be sent out with this token in the header

### `public String createModoToken(String uri)`

Returns a Modo2 Auth Token string for a request that does not have a body (i.e. GET). This method simply calls `public String createModoToken(String uri, String requestBody)` with requestBody equal to empty string.

- `uri` (string) - The request path targeted by this request
