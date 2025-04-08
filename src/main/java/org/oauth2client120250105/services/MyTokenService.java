package org.oauth2client120250105.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyTokenService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2AuthorizedClientProvider authorizedClientProvider;

    public String getAccessToken() {
        OAuth2AuthorizedClient authorizedClient = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Загружаем существующего авторизованного клиента
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            authorizedClient = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
        }

        // Если авторизованного клиента не существует, выбрасываем исключение
        if (authorizedClient == null) {
            log.warn("=== Unable to load OAuth2 client ===");
            throw new AuthenticationServiceException("Unable to load OAuth2 client");
        }

        // Если токен авторизованного клиента просрочен, обновляем его и возвращаем
        if (isTokenExpired(authorizedClient.getAccessToken())) {
            return refreshAccessToken();
        }

        // Или возвращаем токен авторизованного клиента без обновления
        return authorizedClient.getAccessToken().getTokenValue();
    }

    private boolean isTokenExpired(OAuth2AccessToken accessToken) {
        Instant now = Instant.now();
        Instant expiresAt = accessToken.getExpiresAt();
        if (expiresAt == null) {
            // Более консервативный подход - считаем токен истекшим,
            // если нет информации о его сроке действия
            // =======================================================
            // Хотя стандартная интерпретация в Spring Security OAuth2
            // воспринимает отсутствие информации о сроке действия как
            // "токен не имеет срока действия" или "токен действителен бессрочно"
            return true;
        }
        return now.plusSeconds(60).isAfter(expiresAt);
    }

    public String refreshAccessToken() {
        OAuth2AuthorizedClient authorizedClient = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Загружаем существующего авторизованного клиента
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            authorizedClient = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
        }

        // Если авторизованного клиента не существует, выбрасываем исключение
        if (authorizedClient == null) {
            log.warn("=== Unable to load OAuth2 client ===");
            throw new AuthenticationServiceException("Unable to load OAuth2 client");
        }

        // Создаем контекст авторизации с существующим клиентом
        OAuth2AuthorizationContext authorizationContext = OAuth2AuthorizationContext
                .withAuthorizedClient(authorizedClient)
                .principal(authentication)
                .build();

        // Попытка авторизации с обновлением токена
        OAuth2AuthorizedClient refreshedClient = authorizedClientProvider.authorize(authorizationContext);

        // Проверяем успешность обновления токена
        if (refreshedClient != null) {
            // Возвращаем новый токен доступа
            return refreshedClient.getAccessToken().getTokenValue();
        }

        // Выбрасываем исключение, если обновление не удалось
        log.warn("=== Unable to refresh access token ===");
        throw new AuthenticationServiceException("Unable to refresh access token");
    }
}
