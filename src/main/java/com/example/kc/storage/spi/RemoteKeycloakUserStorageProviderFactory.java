package com.example.kc.storage.spi;

import static org.keycloak.provider.ProviderConfigProperty.PASSWORD;
import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

@Slf4j
public class RemoteKeycloakUserStorageProviderFactory implements UserStorageProviderFactory<RemoteKeycloakUserStorageProvider> {

    public static final String SERVER_URL = "server-url";
    public static final String REALM = "realm";
    public static final String CLIENT_ID = "client-id";
    public static final String CLIENT_SECRET = "client-secret";

    @Override
    public RemoteKeycloakUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {

        var keycloakUrl = componentModel.getConfig().getFirst(SERVER_URL);
        var realmName = componentModel.getConfig().getFirst(REALM);
        var clientId = componentModel.getConfig().getFirst(CLIENT_ID);
        var clientSecret = componentModel.getConfig().getFirst(CLIENT_SECRET);

        var keycloak = KeycloakBuilder.builder()
            .serverUrl(keycloakUrl).realm(realmName)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(clientId).clientSecret(clientSecret)
            .build();


        return new RemoteKeycloakUserStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "keycloak-external";
    }

    @Override
    public String getHelpText() {
        return "Remote Keycloak storage provider prototype";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
            .property(SERVER_URL, "KC server url", "Keycloak internal server url", STRING_TYPE, "", List.of())
            .property(REALM, "Realm name", "Target realm name from Keycloak internal", STRING_TYPE, "", List.of(""))
            .property(CLIENT_ID, "Client ID", "Keycloak client ID", STRING_TYPE, "", List.of(""))
            .property(CLIENT_SECRET, "Client secret", "Keycloak client secret", PASSWORD, "", List.of(""))
            .build();
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        if (StringUtils.isBlank(config.getConfig().getFirst(SERVER_URL)) ||
            StringUtils.isBlank(config.getConfig().getFirst(REALM)) ||
            StringUtils.isBlank(config.getConfig().getFirst(CLIENT_ID)) ||
            StringUtils.isBlank(config.getConfig().getFirst(CLIENT_SECRET))) {
            log.warn("one of the mandatory is blank");
            throw new ComponentValidationException("all fields are mandatory");
        }
    }
}
