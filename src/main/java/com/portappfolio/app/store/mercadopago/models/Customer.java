package com.portappfolio.app.store.mercadopago.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class Customer {
    private String email;
    private String name;
    private String surname;
}
