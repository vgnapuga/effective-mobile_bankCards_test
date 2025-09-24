package com.example.bankcards.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.model.user.User;


@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findAllByOwner(final User owner);

    Page<Transfer> findAllByOwner(final User owner, Pageable pageable);

}
