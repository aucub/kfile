package com.example.kfile.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class FileAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        /*HandlerMethod handlerMethod = (HandlerMethod) requestAuthorizationContext.getRequest().getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        PreAuthorize preAuthorizeAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        if (preAuthorizeAnnotation != null) {
            String expression = preAuthorizeAnnotation.value();
        }*/
        return new AuthorizationDecision(true);
    }
}
