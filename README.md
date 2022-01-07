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
This example uses core Java 8 libraries to make a GET and POST request. You can use your preferred method or library.

See this code in action by running the class `./src/com/modo/MainJava8.java` using the Java1.8 JRE. 

### Example Usage - Java 13
This example uses core Java 13 libraries to make a GET and POST request. It differs from the Java 8 example in using the `java.net.http package` that was added in Java 11. You can use your preferred method or library.

See this code in action by running the class `./src/com/modo/MainJava13.java` using the Java1.13 JRE. 

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
