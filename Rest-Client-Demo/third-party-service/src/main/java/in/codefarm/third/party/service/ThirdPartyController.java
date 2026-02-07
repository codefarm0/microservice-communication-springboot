package in.codefarm.third.party.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/third-party")
public class ThirdPartyController {

    private static final Logger log = LoggerFactory.getLogger(ThirdPartyController.class);

    private final List<TestRecord> store = new CopyOnWriteArrayList<>();

    /* ------------------------
       POST - ADD
       ------------------------ */
    @PostMapping
    public ResponseEntity<TestRecord> create(@RequestBody TestRecord request) {
        log.info("POST request received: {}", request);
        simulateRandomFailure("POST");

        TestRecord record = new TestRecord(
                UUID.randomUUID(),
                request.name(),
                request.payload(),
                Instant.now()
        );

        store.add(record);
        log.info("Record created: {}", record);

        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /* ------------------------
       GET - FETCH ALL
       ------------------------ */
    @GetMapping
    public ResponseEntity<List<TestRecord>> getAll() throws InterruptedException {
        log.info("GET all request");
        simulateRandomFailure("GET_ALL");

        Thread.sleep(300); //simulating delay for timeout on client side
        log.info("Returning {} records", store.size());
        return ResponseEntity.ok(store);
    }

    /* ------------------------
       GET - FETCH BY ID
       ------------------------ */
    @GetMapping("/{id}")
    public ResponseEntity<TestRecord> getById(@PathVariable UUID id) {
        log.info("GET by id request: {}", id);
        simulateRandomFailure("GET_BY_ID");

        return store.stream()
                .filter(r -> r.id().equals(id))
                .findFirst()
                .map(record -> {
                    log.info("Record found: {}", record);
                    return ResponseEntity.ok(record);
                })
                .orElseGet(() -> {
                    log.warn("Record not found for id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /* ------------------------
       PUT - UPDATE
       ------------------------ */
    @PutMapping("/{id}")
    public ResponseEntity<TestRecord> update(
            @PathVariable UUID id,
            @RequestBody TestRecord request) {

        log.info("PUT request for id={} payload={}", id, request);
        simulateRandomFailure("PUT");

        for (int i = 0; i < store.size(); i++) {
            TestRecord existing = store.get(i);

            if (existing.id().equals(id)) {
                TestRecord updated = new TestRecord(
                        id,
                        request.name(),
                        request.payload(),
                        Instant.now()
                );

                log.info("Updating record diff | OLD={} | NEW={}", existing, updated);
                store.set(i, updated);

                return ResponseEntity.ok(updated);
            }
        }

        log.warn("Update failed, record not found id={}", id);
        return ResponseEntity.notFound().build();
    }

    /* ------------------------
       DELETE - DELETE
       ------------------------ */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE request for id={}", id);
        simulateRandomFailure("DELETE");

        boolean removed = store.removeIf(r -> r.id().equals(id));

        if (removed) {
            log.info("Record deleted id={}", id);
            return ResponseEntity.noContent().build();
        }

        log.warn("Delete failed, record not found id={}", id);
        return ResponseEntity.notFound().build();
    }

    /* ------------------------
       EXCEPTION HANDLING
       ------------------------ */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailable(ServiceUnavailableException ex) {
        log.error("503 error", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("500 error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /* ------------------------
       CUSTOM EXCEPTION
       ------------------------ */
    static class ServiceUnavailableException extends RuntimeException {
        ServiceUnavailableException(String message) {
            super(message);
        }
    }

    private void simulateRandomFailure(String operation) {
        int chance = ThreadLocalRandom.current().nextInt(0, 10);

        if (chance < 3) { // ~30% failure rate
            log.error("Simulated failure during {}", operation);

            switch (chance) {
                case 0 -> throw new RuntimeException("Simulated 500 Internal Error");
                case 1 -> throw new IllegalStateException("Simulated Illegal State");
                case 2 -> throw new ServiceUnavailableException("Simulated Service Unavailable");
            }
        }
    }

}
