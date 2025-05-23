package com.jane.securitypractice.auth.application;

import com.jane.securitypractice.auth.infrastructure.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
}
