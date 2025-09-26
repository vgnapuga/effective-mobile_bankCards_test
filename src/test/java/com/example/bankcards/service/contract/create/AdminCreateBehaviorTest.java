package com.example.bankcards.service.contract.create;


public interface AdminCreateBehaviorTest extends CreateBehaviorTest {

    void shouldThrowException_whenNotAdmin();

}
