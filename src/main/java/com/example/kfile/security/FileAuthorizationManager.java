package com.example.kfile.security;

import com.example.kfile.service.IFileItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@Component
public class FileAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    IFileItemService fileItemService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        HandlerMethod handlerMethod = (HandlerMethod) requestAuthorizationContext.getRequest().getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        FilePermissionCheck filePermissionCheck = handlerMethod.getMethodAnnotation(FilePermissionCheck.class);

        if (filePermissionCheck != null) {
            FilePermissionCheck.FieldPermission[] fieldPermissions = filePermissionCheck.value();
            Object firstArgument = handlerMethod.getMethod().getParameters()[0];
            for (FilePermissionCheck.FieldPermission fieldPermission : fieldPermissions) {
                try {
                    Field field = firstArgument.getClass().getDeclaredField(fieldPermission.field());
                    field.setAccessible(true);
                    String id = (String) field.get(firstArgument);
                    if (!fileItemService.getPermission(id).equals(fieldPermission.permission()))
                        throw new IllegalArgumentException();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    return new AuthorizationDecision(false);
                }
            }
        }
        return new AuthorizationDecision(true);
    }
}
