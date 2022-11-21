package com.portappfolio.app.store.mercadopago.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BackUrls {

    private String success;
    private String pending;
    private String failure;

}
