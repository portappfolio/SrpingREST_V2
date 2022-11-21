package com.portappfolio.app.models;

import com.portappfolio.app.appUser.phone.Channels;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ConfirmPhoneRequest {
    private final String token;
    private final String prefix;
    private final String number;
    private final Channels channels;
}
