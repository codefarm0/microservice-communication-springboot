package in.codefarm.third.party.service;

import java.time.Instant;
import java.util.UUID;

public record TestRecord(
            UUID id,
            String name,
            String payload,
            Instant updatedAt
    ) {}