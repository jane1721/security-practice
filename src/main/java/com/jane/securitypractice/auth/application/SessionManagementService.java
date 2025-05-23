package com.jane.securitypractice.auth.application;

import com.jane.securitypractice.auth.infrastructure.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionManagementService {
    private final SessionRegistry sessionRegistry;

    public List<SessionInfoResponse> getAllLoggedInUsers() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(principal -> {
                    CustomUserDetails userDetails = (CustomUserDetails) principal;
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    return sessions.stream().map(sessionInformation ->
                            new SessionInfoResponse(
                                    userDetails.getUsername(),
                                    sessionInformation.getSessionId(),
                                    sessionInformation.getLastRequest()
                            )
                    ).toList();
                })
                .flatMap(List::stream)
                .toList();
    }

    public record SessionInfoResponse(
            String username,
            String sessionId,
            Date lastRequestTime
    ) {}

    public List<MySessionInfoResponse> getMySessions(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return sessionRegistry.getAllSessions(userDetails, false).stream()
                .map(session -> new MySessionInfoResponse(session.getSessionId(), session.isExpired()))
                .toList();
    }

    public record MySessionInfoResponse(
            String sessionId,
            boolean expired
    ) {}

    public boolean expireMySession(String sessionId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<SessionInformation> sessionOpt = sessionRegistry.getAllSessions(userDetails, false).stream()
                .filter(session -> session.getSessionId().equals(sessionId))
                .findFirst();

        sessionOpt.ifPresent(SessionInformation::expireNow);
        return sessionOpt.isPresent();
     }
}
