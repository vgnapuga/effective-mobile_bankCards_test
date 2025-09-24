package com.example.bankcards.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.transfer.Transfer;


@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

}
