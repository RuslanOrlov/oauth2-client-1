package org.oauth2client120250105.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckConsentService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MyTokenService tokenService;

    public Boolean isConsentRemoved() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            try {
                String principalName = authentication.getName();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(tokenService.getAccessToken());

                HttpEntity<String> entity = new HttpEntity<>(principalName, headers);

                ResponseEntity<Boolean> response =
                        restTemplate.exchange(
                                "http://authserver:9000/oauth2/check-consent",
                                HttpMethod.POST,
                                entity,
                                Boolean.class
                        );

                /*HttpEntity<Object> entity = new HttpEntity<>(headers);

                ResponseEntity<Boolean> response =
                        restTemplate.exchange(
                                "http://authserver:9000/oauth2/check-consent?principalName=" + principalName,
                                HttpMethod.GET,
                                entity,
                                Boolean.class
                        );*/

                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                }
            } catch (Exception ex) {
                log.error("=== Ошибка при проверке согласия ===", ex);
                // Возвращаем true, если согласие не провалидировано
                // или произошла ошибка. Значение true указывает на
                // отсутствие согласия.
                return true;
            }
        }
        // Возвращаем true, если аутентификация отсутствует.
        // Значение true указывает на отсутствие согласия.
        return true;
    }

}
