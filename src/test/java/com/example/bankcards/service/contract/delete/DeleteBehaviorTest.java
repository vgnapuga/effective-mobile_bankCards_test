package com.example.bankcards.service.contract.delete;


public interface DeleteBehaviorTest {

    void shouldNotThrowException_whenValidRequest();

    void shouldThrowException_whenNullRequesterId();

    void shouldThrowException_whenNullIdToDelete();

    void shouldThrowException_whenInvalidRequesterId();

    void shouldThrowException_whenInvalidIdToDelete();

    void shouldThrowException_whenEntityNotFound();

}
