package com.easyjobs.identity.application.gateway;

import java.util.concurrent.CompletionStage;

public interface LogoutGateway {

    CompletionStage<Void> logout();
}
