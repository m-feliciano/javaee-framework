package com.dev.servlet.core.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class ShortUuidGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return generateShortUuid();
    }

    private String generateShortUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}