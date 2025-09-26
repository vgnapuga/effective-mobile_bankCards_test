package com.example.bankcards.service.contract.update;


public interface UpdateBehaviorTest {

    void shouldReturnEntity_whenValidRequest();

    void shouldThrowException_whenNullRequesterId();

    void shouldThrowException_whenInvalidRequesterId();

    void shouldThrowException_whenEntityNotFound();

}
