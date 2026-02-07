package in.codefarm.client.service.controller;

import in.codefarm.client.service.TestRecord;
import in.codefarm.client.service.ThirdPartyClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client")
public class ClientServiceController {
    private static final Logger log =
            LoggerFactory.getLogger(ClientServiceController.class);

    private final RestClient restClient;

    public ClientServiceController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/records")
    public ResponseEntity<?> getAll() {
        log.info("Client → GET all records");

        List<TestRecord> resp =
                restClient.get()
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                (request, response) -> {
                                    String body = new String(response.getBody().readAllBytes());
                                    log.error("ThirdParty error status={} body={}",
                                            response.getStatusCode(), body);

                                    throw new ThirdPartyClientException(
                                            response.getStatusCode(), body);
                                }
                        )
                        .body(new ParameterizedTypeReference<>() {});

        log.info("Client ← success, size={}", resp.size());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<?> getRecordById( @PathVariable UUID id) {
        log.info("Client → GET record by id");

       TestRecord resp =
                restClient.get()
                        .uri("/{id}", id)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                (request, response) -> {
                                    String body = new String(response.getBody().readAllBytes());
                                    log.error("ThirdParty error status={} body={}",
                                            response.getStatusCode(), body);

                                    throw new ThirdPartyClientException(
                                            response.getStatusCode(), body);
                                }
                        )
                        .body(TestRecord.class);

        log.info("Client ← success, resp={}", resp);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/records")
    public ResponseEntity<?> create(@RequestBody TestRecord request) {
        log.info("Client → POST record request={}", request);

        TestRecord response =
                restClient.post()
                        .body(request)
                        .header("traceId", UUID.randomUUID().toString())
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                (req, res) -> {
                                    String body = new String(res.getBody().readAllBytes());
                                    log.error(
                                            "ThirdParty POST error status={} body={}",
                                            res.getStatusCode(), body
                                    );
                                    throw new ThirdPartyClientException(
                                            res.getStatusCode(), body
                                    );
                                }
                        )
                        .body(TestRecord.class);

        log.info("Client ← POST success response={}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/records")
    public ResponseEntity<?> update(
            @RequestBody TestRecord request) {

        log.info("Client → PUT id={} request={}", request.id(), request);

        TestRecord response =
                restClient.put()
                        .uri("/{id}", request.id())
                        .body(request)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                (req, res) -> {
                                    String body = new String(res.getBody().readAllBytes());
                                    log.error(
                                            "ThirdParty PUT error id={} status={} body={}",
                                            request.id(), res.getStatusCode(), body
                                    );
                                    throw new ThirdPartyClientException(
                                            res.getStatusCode(), body
                                    );
                                }
                        )
                        .body(TestRecord.class);

        log.info("Client ← PUT success response={}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        log.info("Client → DELETE id={}", id);

        restClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> {
                            String body = new String(res.getBody().readAllBytes());
                            log.error(
                                    "ThirdParty DELETE error id={} status={} body={}",
                                    id, res.getStatusCode(), body
                            );
                            throw new ThirdPartyClientException(
                                    res.getStatusCode(), body
                            );
                        }
                )
                .toBodilessEntity();

        log.info("Client ← DELETE success id={}", id);
        return ResponseEntity.noContent().build();
    }



}
