package org.oauth2client120250105.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oauth2client120250105.services.MyTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LogoutController {

    private final MyTokenService tokenService;

    @GetMapping("/my-logout")
    public String logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth,
            @RequestParam(name = "consentRemoved", required = false) boolean consentRemoved
    ) throws ServletException {

        log.info("=== Выход из системы (клиент) ===");

        String accessToken = null;
        try {
            // Получаем токен доступа из сервиса MyTokenService
            accessToken = tokenService.getAccessToken();
        } catch (OAuth2AuthorizationException ex) {
            log.error("=== Ошибка получения токена доступа ===");
            log.error("=== {} ===", ex.getMessage());
        } finally {
            // Очистка аутентификации Spring Security и сессии
            SecurityContextHolder.clearContext();
            new SecurityContextLogoutHandler().logout(request, response, auth);

            // URL для выхода через Authorization Server
            String logoutUrl = "http://authserver:9000/oauth2/logout";

            // URL для возврата в клиентское приложение после успешного выхода
            String postLogoutRedirectUri;
            if (consentRemoved) {
                log.info("=== Согласие было удалено из другого сеанса ===");
                postLogoutRedirectUri = "http://localhost:8080/without-consent-logout";
            } else
                postLogoutRedirectUri = "http://localhost:8080/success-logout";

            // Формируем URL с токеном
            StringBuilder logoutUrlBuilder = new StringBuilder(logoutUrl)
                    .append("?post_logout_redirect_uri=").append(postLogoutRedirectUri);
            if (accessToken != null) {
                logoutUrlBuilder.append("&token=").append(accessToken);
            }

            log.info("=== logoutUrlBuilder.toString() ===" + logoutUrlBuilder.toString());
            // Перенаправление на страницу логаута сервера авторизации
            return "redirect:" + logoutUrlBuilder.toString();
        }
    }
}
