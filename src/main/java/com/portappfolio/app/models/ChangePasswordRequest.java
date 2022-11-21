package com.portappfolio.app.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ChangePasswordRequest {

    private final String token;

    private final Long appUserId;

    private final String newPassword;

}
