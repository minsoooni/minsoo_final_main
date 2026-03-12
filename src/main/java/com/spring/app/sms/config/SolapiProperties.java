package com.spring.app.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solapi")
public class SolapiProperties {

    private String baseUrl;
    private String apiKey;
    private String apiSecret;
    private String fromPhone;
    private boolean enabled;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }

    public String getFromPhone() { return fromPhone; }
    public void setFromPhone(String fromPhone) { this.fromPhone = fromPhone; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}