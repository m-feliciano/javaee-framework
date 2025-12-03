package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.product.ListProductPort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class ListProductUseCase implements ListProductPort {
    @Inject
    private ProductRepositoryPort repositoryPort;
    @Inject
    private AuditPort auditPort;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest payload, String auth, Mapper<Product, U> mapper) {
        log.debug("ListProductUseCase: fetching products pageable with payload {}", payload);

        StopWatch sw = new StopWatch();
        try {
            sw.start();
            IPageable<U> pageable = repositoryPort.getAllPageable(payload, mapper);
            sw.stop();
            auditPort.success("product:list", auth, new AuditPayload<>(payload, pageable.getContent(), Map.of(
                    "total_products", pageable.getTotalElements(),
                    "current_page", pageable.getCurrentPage(),
                    "page_size", pageable.getPageSize(),
                    "time_to_complete in ms", sw.getTime(TimeUnit.MILLISECONDS))));
            return pageable;
        } catch (Exception e) {
            sw.stop();
            auditPort.failure("product:list", null, new AuditPayload<>(payload, null, Map.of("time_to_fail in ms", sw.getTime(TimeUnit.MILLISECONDS))));
            throw e;
        }
    }
}
