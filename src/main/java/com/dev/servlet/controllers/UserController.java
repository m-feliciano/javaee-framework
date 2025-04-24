package com.dev.servlet.controllers;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.dto.UserDTO;
import com.dev.servlet.interfaces.Constraints;
import com.dev.servlet.interfaces.Controller;
import com.dev.servlet.interfaces.IHttpResponse;
import com.dev.servlet.interfaces.RequestMapping;
import com.dev.servlet.interfaces.Validator;
import com.dev.servlet.model.UserModel;
import com.dev.servlet.pojo.domain.User;
import com.dev.servlet.pojo.enums.PerfilEnum;
import com.dev.servlet.pojo.enums.RequestMethod;
import com.dev.servlet.pojo.records.HttpResponse;
import com.dev.servlet.pojo.records.Request;
import lombok.NoArgsConstructor;

import javax.inject.Inject;


@NoArgsConstructor
@Controller(path = "/user")
public final class UserController extends BaseController<User, Long> {

    @Inject
    public UserController(UserModel userModel) {
        super(userModel);
    }

    private UserModel getModel() {
        return (UserModel) super.getBaseModel();
    }

    /**
     * Update user.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with the updated user
     * @throws ServiceException if the user is not found
     */
    @RequestMapping(
            value = "/update/{id}",
            method = RequestMethod.POST,
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    }),
                    @Validator(values = "login", constraints = {
                            @Constraints(isEmail = true, message = "Login must be a valid email")
                    }),
                    @Validator(values = "password", constraints = {
                            @Constraints(minLength = 5, maxLength = 30, message = "Password length must be between {0} and {1} characters")
                    })
            })
    public IHttpResponse<Void> update(Request request) throws ServiceException {
        UserDTO user = this.getModel().update(request);
        // OK
        return super.newHttpResponse(204, null, super.redirectTo(user.getId()));
    }

    /**
     * Delete user.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} with no content {@linkplain Void}
     */
    @RequestMapping(
            value = "/delete/{id}",
            method = RequestMethod.POST,
            roles = {
                    PerfilEnum.ADMIN
            },
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<Void> delete(Request request) throws ServiceException {
        this.getModel().delete(request);

        return HttpResponse.ofNext(super.forwardTo("formLogin"));
    }

    /**
     * Create user.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse}
     */
    @RequestMapping(
            value = "/registerUser",
            method = RequestMethod.POST,
            requestAuth = false,
            validators = {
                    @Validator(values = "login", constraints = {
                            @Constraints(isEmail = true, message = "Login must be a valid email")
                    }),
                    @Validator(values = {"password", "confirmPassword"},
                            constraints = {
                                    @Constraints(minLength = 5, message = "Password must have at least {0} characters"),
                                    @Constraints(maxLength = 30, message = "Password must have at most {0} characters"),
                            }),
            })
    public IHttpResponse<Void> register(Request request) throws ServiceException {
        this.getModel().register(request);
        // Created
        return super.newHttpResponse(201, null, "redirect:/api/v1/login/form");
    }

    /**
     * List the user by id.
     *
     * @param request {@linkplain Request}
     * @return {@linkplain IHttpResponse} of {@linkplain UserDTO}
     */
    @RequestMapping(
            value = "/list/{id}",
            validators = {
                    @Validator(values = "id", constraints = {
                            @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                    })
            })
    public IHttpResponse<UserDTO> listById(Request request) throws ServiceException {
        UserDTO user = this.getModel().findById(request);
        // OK
        return super.okHttpResponse(user, super.forwardTo("formListUser"));
    }
}
