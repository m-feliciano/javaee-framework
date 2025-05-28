package com.dev.servlet.domain.service;
import com.dev.servlet.domain.transfer.dto.DataTransferObject;
import com.dev.servlet.domain.transfer.request.Request;
import com.dev.servlet.domain.repository.ICrudRepository;

public interface IBaseService<T, ID> extends ICrudRepository<T, ID> {
    T getEntity(Request request);
    T toEntity(Object object);
    Class<? extends DataTransferObject<ID>> getDataMapper();
}
