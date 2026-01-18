package com.easyjobs.identity.domain.model;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

import com.easyjobs.identity.domain.model.exception.InsufficientCreditsException;
import com.easyjobs.identity.domain.model.exception.InvalidCreditsOperationException;
import com.easyjobs.identity.domain.model.exception.InvalidUserDataException;

public final class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private final String stripeCustomerId;
    private final int creditsBalance;
    private final OffsetDateTime createdAt;

    public User(UUID id, String email, String passwordHash, String fullName, String stripeCustomerId, int creditsBalance, OffsetDateTime createdAt) {
        this.id = requireNonNull(id, "id não pode ser nulo");
        this.email = normalizeEmail(email);
        this.passwordHash = requireNonBlank(passwordHash, "hash da senha");
        this.fullName = normalizeFullName(fullName);
        if (creditsBalance < 0) {
            throw new InvalidUserDataException("saldo de créditos não pode ser negativo");
        }
        
        this.stripeCustomerId = stripeCustomerId;
        this.creditsBalance = creditsBalance;
        this.createdAt = requireNonNull(createdAt, "data de criação não pode ser nula");
    }

    public static User newUser(UUID id, String email, String passwordHash, String fullName, OffsetDateTime createdAt) {
        return new User(id, email, passwordHash, fullName, null, 0, createdAt);
    }

    public User consumeCredit() {
        if (creditsBalance <= 0) {
            throw new InsufficientCreditsException("Saldo de créditos insuficiente para consumir.");
        }
        return new User(id, email, passwordHash, fullName, stripeCustomerId, creditsBalance - 1, createdAt);
    }

    public User addCredits(int amount) {
        if (amount <= 0) {
            throw new InvalidCreditsOperationException("Quantidade de créditos para adicionar deve ser maior que zero.");
        }
        if (creditsBalance > Integer.MAX_VALUE - amount) {
            throw new InvalidCreditsOperationException("Saldo de créditos excede o limite permitido.");
        }
        return new User(id, email, passwordHash, fullName, stripeCustomerId, creditsBalance + amount, createdAt);
    }

    public boolean isEligibleForPayments() {
        return stripeCustomerId != null && !stripeCustomerId.isBlank();
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserDataException(field + " não pode ser vazio.");
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new InvalidUserDataException(message);
        }
        return value;
    }

    private static String normalizeEmail(String email) {
        return requireNonBlank(email, "email").trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeFullName(String fullName) {
        return requireNonBlank(fullName, "nome completo").trim();
    }

    public UUID id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String fullName() {
        return fullName;
    }

    public String stripeCustomerId() {
        return stripeCustomerId;
    }

    public int creditsBalance() {
        return creditsBalance;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }
}
