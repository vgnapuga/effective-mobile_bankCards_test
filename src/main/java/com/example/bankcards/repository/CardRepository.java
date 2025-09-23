package com.example.bankcards.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.card.Card;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

}
