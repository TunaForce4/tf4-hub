package com.tunaforce.hub.common.config;

import com.tunaforce.hub.common.exception.ApplicationException;
import com.tunaforce.hub.common.exception.HubException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            try {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String userIdHeader = request.getHeader("X-User-Id");

                    if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
                        return Optional.of(UUID.fromString(userIdHeader));
                    }
                }
            } catch (Exception e) {
                throw new ApplicationException(HubException.USER_NOT_FOUND);
            }

            // 헤더에서 가져올 수 없는 경우 기본값 반환
            return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        };
    }
}
