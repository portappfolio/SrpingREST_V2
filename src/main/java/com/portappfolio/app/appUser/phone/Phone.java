package com.portappfolio.app.appUser.phone;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Phone {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne
    private Prefix prefix;

    @Column(nullable = false)
    private String number;

    @Column
    private Boolean confirmed = false;

    @Enumerated(EnumType.STRING)
    private Channels channel;

    @Enumerated(EnumType.STRING)
    private Types type;

    @Column(nullable = true,name = "confirmed_at")
    private LocalDateTime confirmedAt = LocalDateTime.now();

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Phone(Prefix prefix, String number, Channels channel, Types type, Boolean confirmed) {
        this.prefix = prefix;
        this.number = number;
        this.channel = channel;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if(confirmed){
            this.confirmedAt = LocalDateTime.now();
            this.confirmed = confirmed;
        }
    }
}
