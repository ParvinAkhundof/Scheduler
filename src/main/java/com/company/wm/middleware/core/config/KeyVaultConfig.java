package com.company.wm.middleware.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

@Configuration
// @Import(AzureKeyVaultSecretAutoConfiguration.class)
public class KeyVaultConfig {

    @Value("${azure.keyvault.uri}")
    private String keyVaultUri;

    @Value("${azure.keyvault.tenant-id}")
    private String tenantId;

    @Value("${azure.keyvault.client-id}")
    private String clientId;

    @Value("${azure.keyvault.client-secret}")
    private String clientSecret;

    @Bean
    public SecretClient secretClient() {
        try {
            // Build the ClientSecretcredential using Azure credentials
            ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                    .tenantId(tenantId)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            // Create and return the SecretClient to interact with Azure Key Vault
            return new SecretClientBuilder()
                    .vaultUrl(keyVaultUri)
                    .credential(clientSecretCredential)
                    .buildClient();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error initializing SecretClient: " + e.getMessage(), e);
        }
    }
}
