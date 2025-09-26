package com.example.bankcards.service.contract.create;


public interface CreateBehaviorTest {

    void shouldReturnEntity_whenValidRequest();

    void shouldThrowException_whenNullId();

    void shouldThrowException_whenInvalidId();

}
