package com.dev.servlet.domain.transfer.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

public record UserCreateRequest(@Email String login,
                                @Min(6) String password,
                                @Min(6) String confirmPassword) {
}
