package com.easyjobs.shared.infrastructure;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

public final class UuidGenerator {

    private UuidGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID generateV7() {
        return UuidCreator.getTimeOrdered();
    }
}
