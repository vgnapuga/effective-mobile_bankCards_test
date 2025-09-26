package com.example.bankcards.service.contract.delete;


public interface AdminDeleteBehaviorTest extends DeleteBehaviorTest {

    void shouldThrowException_whenNotAdmin();

}
