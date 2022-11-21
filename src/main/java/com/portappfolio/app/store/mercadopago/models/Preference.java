package com.portappfolio.app.store.mercadopago.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Preference {
    private String additional_info;
    private String auto_return; //all
    private BackUrls back_urls;
    private String date_of_expiration; //fecha expiración para pago en efectivo
    private Boolean expires; //Si expira o no
    private String external_reference; //Codigo de orden
    private List<Item> items;
    private String statement_descriptor; //Texto que se va a mostrar en extracto del pagado
    private Boolean binary_mode;
    private Customer payer;

}
