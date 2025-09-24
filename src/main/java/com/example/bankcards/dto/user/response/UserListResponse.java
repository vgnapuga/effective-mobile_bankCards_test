package com.example.bankcards.dto.user.response;


import java.util.List;


public record UserListResponse(List<UserResponse> users, Long totalCount, int page, int size) {
}
