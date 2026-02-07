package in.codefarm.client.service;

import org.springframework.http.HttpStatusCode;

public class ThirdPartyClientException extends RuntimeException {
    public ThirdPartyClientException(HttpStatusCode statusCode, String body) {
        super(statusCode.toString());
    }
}
