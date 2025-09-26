package com.example.bankcards.service.contract.read;


public interface ReadBehaviorTest {

    void shouldReturnEntity_whenValidRequest();

    void shouldThrowException_whenNullRequesterId();

    void shouldThrowException_whenNullIdToRead();

    void shouldThrowException_whenInvalidRequesterId();

    void shouldThrowException_whenInvalidIdToRead();

    void shouldThrowException_whenEntityNotFound();

}
