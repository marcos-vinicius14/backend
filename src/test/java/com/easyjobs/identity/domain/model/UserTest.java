package com.easyjobs.identity.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.easyjobs.identity.domain.model.exception.InsufficientCreditsException;
import com.easyjobs.identity.domain.model.exception.InvalidCreditsOperationException;
import com.easyjobs.identity.domain.model.exception.InvalidUserDataException;

class UserTest {

    @Test
    void newUserShouldInitializeDefaults() {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.parse("2024-01-01T00:00:00Z");

        User user = User.newUser(id, "USER@EXAMPLE.COM", "hash", "Ada Lovelace", createdAt);

        assertEquals(id, user.id());
        assertEquals("user@example.com", user.email());
        assertEquals("Ada Lovelace", user.fullName());
        assertEquals("hash", user.passwordHash());
        assertEquals(0, user.creditsBalance());
        assertEquals(null, user.stripeCustomerId());
        assertEquals(createdAt, user.createdAt());
    }

    @Test
    void constructorShouldRejectNullId() {
        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> new User(null, "user@example.com", "hash", "Ada Lovelace", null, 0, OffsetDateTime.now()));

        assertEquals("id não pode ser nulo", exception.getMessage());
    }

    @Test
    void constructorShouldRejectNullCreatedAt() {
        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> new User(UUID.randomUUID(), "user@example.com", "hash", "Ada Lovelace", null, 0, null));

        assertEquals("data de criação não pode ser nula", exception.getMessage());
    }

    @Test
    void constructorShouldRejectBlankEmail() {
        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> new User(UUID.randomUUID(), "   ", "hash", "Ada Lovelace", null, 0, OffsetDateTime.now()));

        assertEquals("email não pode ser vazio.", exception.getMessage());
    }

    @Test
    void constructorShouldNormalizeEmail() {
        User user = new User(
                UUID.randomUUID(),
                "  USER@Example.COM ",
                "hash",
                "Ada Lovelace",
                null,
                0,
                OffsetDateTime.parse("2024-01-01T00:00:00Z"));

        assertEquals("user@example.com", user.email());
    }

    @Test
    void constructorShouldRejectBlankFullName() {
        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> new User(UUID.randomUUID(), "user@example.com", "hash", " ", null, 0, OffsetDateTime.now()));

        assertEquals("nome completo não pode ser vazio.", exception.getMessage());
    }

    @Test
    void constructorShouldNormalizeFullName() {
        User user = new User(
                UUID.randomUUID(),
                "user@example.com",
                "hash",
                "  Ada Lovelace  ",
                null,
                0,
                OffsetDateTime.parse("2024-01-01T00:00:00Z"));

        assertEquals("Ada Lovelace", user.fullName());
    }

    @Test
    void constructorShouldRejectBlankPasswordHash() {
        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> new User(UUID.randomUUID(), "user@example.com", " ", "Ada Lovelace", null, 0, OffsetDateTime.now()));

        assertEquals("hash da senha não pode ser vazio.", exception.getMessage());
    }

    @Test
    void constructorShouldRejectNegativeCreditsBalance() {
        InvalidUserDataException exception = assertThrows(
                InvalidUserDataException.class,
                () -> new User(UUID.randomUUID(), "user@example.com", "hash", "Ada Lovelace", null, -1, OffsetDateTime.now()));

        assertEquals("saldo de créditos não pode ser negativo", exception.getMessage());
    }

    @Test
    void consumeCreditShouldDecrementBalance() {
        User user = buildUser(2, "stripe_123");

        User updated = user.consumeCredit();

        assertEquals(1, updated.creditsBalance());
        assertEquals(user.id(), updated.id());
    }

    @Test
    void consumeCreditShouldThrowWhenNoCredits() {
        User user = buildUser(0, "stripe_123");

        InsufficientCreditsException exception = assertThrows(InsufficientCreditsException.class, user::consumeCredit);

        assertEquals("Saldo de créditos insuficiente para consumir.", exception.getMessage());
    }

    @Test
    void addCreditsShouldIncreaseBalance() {
        User user = buildUser(1, "stripe_123");

        User updated = user.addCredits(3);

        assertEquals(4, updated.creditsBalance());
    }

    @Test
    void addCreditsShouldRejectNonPositiveAmount() {
        User user = buildUser(1, "stripe_123");

        InvalidCreditsOperationException exception = assertThrows(
                InvalidCreditsOperationException.class,
                () -> user.addCredits(0));

        assertEquals("Quantidade de créditos para adicionar deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void addCreditsShouldRejectOverflow() {
        User user = buildUser(Integer.MAX_VALUE, "stripe_123");

        InvalidCreditsOperationException exception = assertThrows(
                InvalidCreditsOperationException.class,
                () -> user.addCredits(1));

        assertEquals("Saldo de créditos excede o limite permitido.", exception.getMessage());
    }

    @Test
    void isEligibleForPaymentsShouldReturnTrueWhenStripeCustomerIdIsPresent() {
        User user = buildUser(1, "stripe_123");

        assertTrue(user.isEligibleForPayments());
    }

    @Test
    void isEligibleForPaymentsShouldReturnFalseWhenStripeCustomerIdIsBlank() {
        User user = buildUser(1, "   ");

        assertFalse(user.isEligibleForPayments());
    }

    @Test
    void isEligibleForPaymentsShouldReturnFalseWhenStripeCustomerIdIsNull() {
        User user = buildUser(1, null);

        assertFalse(user.isEligibleForPayments());
    }

    private User buildUser(int creditsBalance, String stripeCustomerId) {
        return new User(
                UUID.randomUUID(),
                "user@example.com",
                "hash",
                "Ada Lovelace",
                stripeCustomerId,
                creditsBalance,
                OffsetDateTime.parse("2024-01-01T00:00:00Z"));
    }
}
