package com.example.kc.storage.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.CredentialValidationOutput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class RemoteKeycloakUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator, CredentialInputUpdater {

    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    // map of loaded users in this transaction
    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    @Override
    public boolean updateCredential(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        log.info("updateCredential called");

        // If remote password policy fails, throw ModelException with the exact same message
        return false;

    }

    @Override
    public void disableCredentialType(RealmModel realmModel, UserModel userModel, String s) {
        log.info("disableCredentialType was called for {}", s);
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realmModel, UserModel userModel) {
        log.info("getDisableableCredentialTypesStream was called");
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("supportsCredentialType was called with {}", credentialType);
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        log.info("isConfiguredFor was called for {}", credentialType);
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType())) {
            log.warn("isValid called with unsupported credential type '{}'", credentialInput.getType());
            return false;
        }

        log.info("isValid was called with username: {}, email: {}, credId: {} password: {}",
            userModel.getUsername(),
            userModel.getEmail(),
            credentialInput.getCredentialId(),
            credentialInput.getChallengeResponse());

        return false;
    }

    @Override
    public void close() {
        //NO-OP
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String userId) {
        log.info("getUserById was called for {}", userId);
        StorageId storageId = new StorageId(userId);
        String username = storageId.getExternalId();
        return getUserByUsername(realmModel, username);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String username) {
        log.info("getUserByUsername was called for {}", username);

        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        log.info("getUserByEmail was called for {}", email);
        return null;
    }

    @Override
    public CredentialValidationOutput getUserByCredential(RealmModel realm, CredentialInput input) {
        return UserLookupProvider.super.getUserByCredential(realm, input);
    }

    protected UserModel createAdapter(RealmModel realm, UserRepresentation user) {
        return new AbstractUserAdapter(keycloakSession, realm, componentModel) {
            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public SubjectCredentialManager credentialManager() {
                return null;
            }
        };
    }

}
