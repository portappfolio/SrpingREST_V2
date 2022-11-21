package com.portappfolio.app.email;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class SengridConfig {

    private String key;

    @Autowired
    public SengridConfig(@Value("${sengrid.key}")String key){
        this.key = key;
    }

    @Bean
    public SendGrid getSendGrid(){
        return new SendGrid(this.key);
    }
}
