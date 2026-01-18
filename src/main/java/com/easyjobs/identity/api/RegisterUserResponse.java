package com.easyjobs.identity.api;

import java.util.Set;

public record RegisterUserResponse(String id, String email, Set<String> roles) {
}
