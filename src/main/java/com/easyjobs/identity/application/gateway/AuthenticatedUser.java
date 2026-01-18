package com.easyjobs.identity.application.gateway;

import java.util.Set;

public record AuthenticatedUser(String username, Set<String> roles) {
}
