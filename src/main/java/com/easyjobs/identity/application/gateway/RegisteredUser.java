package com.easyjobs.identity.application.gateway;

import java.util.Set;

public record RegisteredUser(String id, String email, Set<String> roles) {
}
