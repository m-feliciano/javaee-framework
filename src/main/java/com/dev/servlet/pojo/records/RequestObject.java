package com.dev.servlet.pojo.records;

// Immutable class

/**
 * This class represents a request object.
 * <br>
 * It contains the following fields: <br>
 * - action: the action to be performed <br>
 * - service: the service to be used <br>
 * - id: the resource id <br>
 * - token: the token <br>
 * - query: the query object <br>
 *
 * @since 1.3.5
 */
public record RequestObject(String service,
                            String action,
                            Long id,
                            Query query,
                            String token) {
}