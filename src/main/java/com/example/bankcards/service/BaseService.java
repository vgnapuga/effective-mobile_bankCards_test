package com.example.bankcards.service;


import org.springframework.data.domain.Pageable;

import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.DomainValidationException;


public abstract class BaseService {

    private static final String NULL_ID_MESSAGE = "Entity id is <null>";

    private static final String TEMPLATE_LESS_THAN_ONE_ID_MESSAGE = "Entity id must be positive (actual: %d)";
    private static final String TEMPLATE_NEGATIVE_PAGE_MESSAGE = "Pagination page must be positive or zero (actual: %d)";
    private static final String TEMPLATE_LESS_THAN_ONE_SIZE_MESSAGE = "Pagination size must be positive (actual: %d)";

    protected final void validateId(final Long id) {
        if (id == null)
            throw new DomainValidationException(NULL_ID_MESSAGE);

        if (id <= 0)
            throw new BusinessRuleViolationException(String.format(TEMPLATE_LESS_THAN_ONE_ID_MESSAGE, id));
    }

    protected final void validatePagination(final Pageable pageable) {
        if (pageable == null)
            throw new BusinessRuleViolationException("Pageable is required");

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        if (page < 0)
            throw new BusinessRuleViolationException(String.format(TEMPLATE_NEGATIVE_PAGE_MESSAGE, page));

        if (size <= 0)
            throw new BusinessRuleViolationException(String.format(TEMPLATE_LESS_THAN_ONE_SIZE_MESSAGE, size));
    }

}
