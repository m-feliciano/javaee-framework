package com.dev.servlet.domain.service;
import com.dev.servlet.domain.transfer.dto.CategoryDTO;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.core.exception.ServiceException;
import java.util.Collection;

public interface ICategoryService {
    CategoryDTO register(Request request) throws ServiceException;
    CategoryDTO update(Request request) throws ServiceException;
    CategoryDTO getById(Request request) throws ServiceException;
    Collection<CategoryDTO> list(Request request);
    boolean delete(Request request) throws ServiceException;
}
