package com.example.bankcards.service.contract.read;


public interface ReadBehaviorTest {

    void shouldReturnEntity_whenValidRequest();

    void shouldThrowException_whenNullRequesterId();

    void shouldThrowException_whenNullIdToRead();

    void shouldThrowException_whenInvalidRequesterId(Long id);

    void shouldThrowException_whenInvalidIdToRead(Long id);

    void shouldThrowException_whenEntityNotFound();

}
