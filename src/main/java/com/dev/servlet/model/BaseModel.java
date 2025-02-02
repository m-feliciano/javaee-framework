package com.dev.servlet.model;

import com.dev.servlet.dao.BaseDAO;
import com.dev.servlet.interfaces.CrudRepository;
import com.dev.servlet.pojo.Identifier;
import com.dev.servlet.pojo.User;
import com.dev.servlet.pojo.records.KeyPair;
import com.dev.servlet.pojo.records.Pagination;
import com.dev.servlet.pojo.records.Request;
import com.dev.servlet.utils.ClassUtil;
import com.dev.servlet.utils.CryptoUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Base Business
 * <p>
 * This layer is supposed to be the business layer, where we handle the request that the specializations will execute.
 *
 * @param <T> the entity extends {@linkplain Identifier} of {@linkplain K}
 * @param <K> the entity id
 * @implNote You should extend this class and provide a DAO specialization, which extends {@linkplain BaseDAO}.
 * @see BaseDAO
 */
@Slf4j
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public abstract class BaseModel<T extends Identifier<K>, K> implements CrudRepository<T, K> {

    protected BaseDAO<T, K> baseDAO;

    protected BaseModel(BaseDAO<T, K> baseDAO) {
        this.baseDAO = baseDAO;
    }

    @Override
    public Collection<T> findAll(T object) {
        return baseDAO.findAll(object);
    }

    @Override
    public T find(T object) {
        return baseDAO.find(object);
    }

    @Override
    public T findById(K id) {
        return baseDAO.findById(id);
    }

    @Override
    public void save(T object) {
        baseDAO.save(object);
    }

    @Override
    public T update(T object) {
        return baseDAO.update(object);
    }

    @Override
    public void delete(T object) {
        baseDAO.delete(object);
    }

    @Override
    public Collection<K> findAllOnlyIds(T object) {
        return baseDAO.findAllOnlyIds(object);
    }

    @Override
    public Collection<T> getAllPageable(Collection<K> ids, Pagination pagination) {
        return baseDAO.getAllPageable(ids, pagination);
    }

    /**
     * Retrieve the transfer class
     *
     * @return {@linkplain Class} of {@linkplain Identifier} type {@linkplain K}
     */
    protected abstract Class<? extends Identifier<K>> getTransferClass();

    /**
     * Convert the object to the entity
     *
     * @param object the object to be converted
     * @return {@linkplain T} the entity
     */
    protected abstract T toEntity(Object object);

    protected User getUser(String token) {
        return CryptoUtils.getUser(token);
    }

    /**
     * Retrieve the base entity from the request
     *
     * @param request {@linkplain Request}
     * @return {@linkplain T} the entity
     * @author marcelo.feliciano
     */
    protected T getEntity(Request request) {
        return Optional.ofNullable(getTransferObject(request))
                .map(this::toEntity)
                .orElse(null);
    }

    /**
     * Retrieve the base transfer object from the request
     *
     * @param request {@linkplain Request}
     * @return {@linkplain T} the entity
     * @author marcelo.feliciano
     */
    protected Object getTransferObject(Request request) {
        Optional<? extends Identifier<K>> optional = ClassUtil.createInstance(getTransferClass());

        String entityId = request.getEntityId();
        List<KeyPair> parameters = request.getBody();

        return optional
                .map(entity -> fillObjectData(entity, entityId, parameters))
                .orElse(null);
    }

    /**
     * Convert the transfer object to the entity
     *
     * @param object     the transfer object {@linkplain U}
     * @param id         the entity id
     * @param parameters {@linkplain List} of {@linkplain KeyPair}
     * @param <U>        the transfer object
     * @return {@linkplain Identifier} of {@linkplain K}-
     * @author marcelo.feliciano
     */
    private <U extends Identifier<K>> Identifier<K> fillObjectData(U object, String id, List<KeyPair> parameters) {
        if (id != null) {
            Class<K> typeK = ClassUtil.extractType(this.getClass(), 2);
            K objectK = ClassUtil.castWrapper(typeK, id);
            object.setId(objectK);
        }

        ClassUtil.fillObject(object, parameters);
        return object;
    }

}