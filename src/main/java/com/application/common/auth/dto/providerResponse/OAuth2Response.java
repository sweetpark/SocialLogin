package com.application.common.auth.dto.providerResponse;

public interface OAuth2Response {
    public String getProvider();
    public String getProviderId();
    public String getEmail();
    public String getName();
}
