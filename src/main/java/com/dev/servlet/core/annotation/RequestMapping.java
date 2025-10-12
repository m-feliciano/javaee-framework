package com.dev.servlet.core.annotation;

import com.dev.servlet.domain.model.enums.RequestMethod;
import com.dev.servlet.domain.model.enums.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps HTTP requests to specific controller methods based on URL patterns and HTTP methods.
 * This annotation defines the routing configuration for web endpoints, including
 * authentication requirements, validation rules, and role-based access control.
 * 
 * <p>The framework uses this annotation to:
 * <ul>
 *   <li>Route incoming HTTP requests to appropriate handler methods</li>
 *   <li>Apply authentication and authorization queries</li>
 *   <li>Execute validation rules before method execution</li>
 *   <li>Support API versioning</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @Controller("UserController")
 * public class UserController {
 *     
 *     @RequestMapping(
 *         value = "/users", 
 *         method = RequestMethod.GET,
 *         requestAuth = true,
 *         roles = {RoleType.ADMIN, RoleType.USER}
 *     )
 *     public  IHttpResponse<UserDTO> getUsers(Request request) {
 *         // Handle authenticated GET /users request
 *     }
 *     
 *     @RequestMapping(
 *         value = "/users", 
 *         method = RequestMethod.POST,
 *         validators = {
 *             @Validator(values = {"username"}, constraints = @Constraints(notNullOrEmpty = true)),
 *             @Validator(values = {"email"}, constraints = @Constraints(isEmail = true))
 *         },
 *         apiVersion = "v2"
 *     )
 *     public IHttpResponse<UserDTO> createUser(Request request) {
 *         // Handle validated POST /v2/users request
 *     }
 * }
 * }
 * </pre>
 * 
 * @since 1.0
 * @see Controller
 * @see RequestMethod
 * @see RoleType
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();

    RequestMethod method() default RequestMethod.GET;

    Class<?> jsonType() default Void.class;

    boolean validate() default false;
    boolean requestAuth() default true;
    String apiVersion() default "v1";
    RoleType[] roles() default {};
}
