package com.easyjobs.identity.application.usecase;

import java.util.concurrent.CompletionStage;

import com.easyjobs.identity.application.gateway.LogoutGateway;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogoutUserUseCase {

    private static final Logger LOG = Logger.getLogger(LogoutUserUseCase.class);

    private final LogoutGateway gateway;

    public LogoutUserUseCase(LogoutGateway gateway) {
        this.gateway = gateway;
    }

    public CompletionStage<Void> execute() {
        return gateway.logout()
                .handle((ignored, failure) -> {
                    if (failure != null) {
                        LOG.warn("Falha ao encerrar a sessão no provedor OIDC.", failure);
                    }
                    return null;
                });
    }
}
