package com.dev.servlet.pojo.records;

// Immutable class

/**
 * This class represents a request object.
 * <br>
 * It contains the following fields: <br>
 * - action: the action to be performed <br>
 * - service: the service to be used <br>
 * - resourceId: the resource id <br>
 * - token: the token <br>
 * - pagination: the pagination object <br>
 *
 * @since 1.3.5
 */
public record RequestObject(String action,
                            String service,
                            Long resourceId,
                            String token,
                            Pagable pagination) {
}