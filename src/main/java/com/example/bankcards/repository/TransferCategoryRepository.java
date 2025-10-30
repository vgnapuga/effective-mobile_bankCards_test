package com.example.bankcards.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.transfer.category.CategoryName;
import com.example.bankcards.model.transfer.category.TransferCategory;


@Repository
public interface TransferCategoryRepository extends JpaRepository<TransferCategory, Long> {

    Optional<TransferCategory> findByName(CategoryName name);

    boolean existsByName(CategoryName name);

}