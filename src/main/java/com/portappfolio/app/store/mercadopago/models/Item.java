package com.portappfolio.app.store.mercadopago.models;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class Item {

    private String title;

    private String description;

    private String picture_url;

    private String category_id;

    private Long quantity;

    private String currency_id;

    private Long unit_price;

}
