package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.product.ListProductUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.persistence.repository.ProductRepository;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
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
public class ListProductUseCase implements ListProductUseCasePort {
    @Inject
    private ProductRepository productRepository;
    @Inject
    private AuditPort auditPort;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest payload, Mapper<Product, U> mapper) {
        log.debug("ListProductUseCase: fetching products pageable with payload {}", payload);

        StopWatch sw = new StopWatch();
        try {
            sw.start();
            IPageable<U> pageable = productRepository.getAllPageable(payload, mapper);
            sw.stop();
            auditPort.success("product:list", null, new AuditPayload<>(payload, pageable.getContent(), Map.of(
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
