package com.dev.servlet.application.transfer.request;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record UserCreateRequest(@Email String login,
                                @Length(min = 8,
                                        max = 14,
                                        message = "Password must be between {min} and {max} characters") String password,
                                @Length(min = 8,
                                        max = 14,
                                        message = "Password must be between {min} and {max} characters") String confirmPassword) {
}
