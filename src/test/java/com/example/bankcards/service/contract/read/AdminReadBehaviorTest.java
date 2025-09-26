package com.example.bankcards.service.contract.read;


public interface AdminReadBehaviorTest extends ReadBehaviorTest {

    void shouldThrowException_whenNotAdmin();

}
