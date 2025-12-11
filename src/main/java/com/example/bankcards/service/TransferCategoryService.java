package com.example.bankcards.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.transfer.category.CategoryName;
import com.example.bankcards.model.transfer.category.TransferCategory;
import com.example.bankcards.repository.TransferCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransferCategoryService extends BaseService {

    private final TransferCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public TransferCategory getCategoryById(Long categoryId) {
        validateId(categoryId);
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Category with id=%d not found", categoryId)));
    }

    @Transactional(readOnly = true)
    public TransferCategory getCategoryByName(final CategoryName name) {
        return categoryRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Category with name=%s not found", name)));
    }

    @Transactional(readOnly = true)
    public List<TransferCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

}
