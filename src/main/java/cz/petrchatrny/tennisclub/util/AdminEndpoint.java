package cz.petrchatrny.tennisclub.util;

import cz.petrchatrny.tennisclub.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that combines the required user role to use
 * for endpoint protection and the required documentation annotation in the swagger.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME)
public @interface AdminEndpoint {
}
