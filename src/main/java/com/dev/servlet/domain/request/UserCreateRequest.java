package com.dev.servlet.domain.request;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

public record UserCreateRequest(@Email String login,
                                @Length(min = 6,
                                        max = 12,
                                        message = "Password must be between {min} and {max} characters") String password,
                                @Length(min = 6,
                                        max = 12,
                                        message = "Password must be between {min} and {max} characters") String confirmPassword) {
}
