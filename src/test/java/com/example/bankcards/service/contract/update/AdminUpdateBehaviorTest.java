package com.example.bankcards.service.contract.update;


public interface AdminUpdateBehaviorTest extends UpdateBehaviorTest {

    void shouldThrowException_whenNotAdmin();

    void shouldThrowException_whenNullIdToUpdate();

    void shouldThrowException_whenInvalidIdToUpdate(Long id);

}
