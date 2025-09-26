package com.example.bankcards.service.contract.update;


public interface UpdateBehaviorTest {

    void shouldReturnEntity_whenValidRequest();

    void shouldThrowException_whenNullRequesterId();

    void shouldThrowException_whenInvalidRequesterId(Long id);

    void shouldThrowException_whenEntityNotFound();

}
