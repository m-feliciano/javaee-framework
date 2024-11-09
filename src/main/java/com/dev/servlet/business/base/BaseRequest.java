package com.dev.servlet.business.base;

import com.dev.servlet.dto.ServiceException;
import com.dev.servlet.interfaces.IService;
import com.dev.servlet.pojo.User;
import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.utils.CryptoUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

public abstract class BaseRequest {
    protected static final String INVALID = "invalid";
    protected static final String CREATE = "create"; // Create resource
    protected static final String REGISTER = "register"; //create a new user
    protected static final String LIST = "list";
    protected static final String UPDATE = "update";
    protected static final String NEW = "new"; // Forward to create
    protected static final String REGISTER_PAGE = "registerPage"; // Forward to create user
    protected static final String EDIT = "edit";
    protected static final String DELETE = "delete";
    protected static final String FORWARD_PAGES_FORM_LOGIN = "forward:pages/formLogin.jsp";
    protected final Logger logger;
    protected final Gson gson;

    protected BaseRequest() {
        this.logger = LoggerFactory.getLogger(BaseRequest.class);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setPrettyPrinting()
                .create();
    }

    /**
     * Return the user from cache
     *
     * @param request
     * @return {@link User}
     */
    protected static User getUser(StandardRequest request) {
        return CryptoUtils.getUser(request.getToken());
    }

    /**
     * Return the service exception.
     *
     * @param id
     */
    protected void throwResourceNotFoundException(Long id) throws ServiceException {
        String serviceName = this.getClass().getAnnotation(IService.class).value();
        throw new ServiceException(HttpServletResponse.SC_NOT_FOUND, "The " + serviceName + " with id " + id + " was not found.");
    }
}
