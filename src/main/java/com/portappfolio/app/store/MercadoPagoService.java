package com.portappfolio.app.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.portappfolio.app.store.mercadopago.models.BackUrls;
import com.portappfolio.app.store.mercadopago.models.Customer;
import com.portappfolio.app.store.mercadopago.models.Preference;
import com.portappfolio.app.store.mercadopago.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@PropertySource("classpath:application.yml")
public class MercadoPagoService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String urlApi;
    private static String accessToken;
    private static String pathGetPaymentMethods;
    private static String pathGetIdentificationTypes;
    private static String pathPostCreateClient;
    private static String pathGetClient;
    private static String pathPostCreatePreference;
    private static String backUrlSuccess;
    private static String backUrlPending;
    private static String backUrlFailure;
    private static String autoReturn;
    private static Boolean expires;
    private static String statementDescription;
    private static Boolean binaryMode;


    @Autowired
    public MercadoPagoService(
            @Value("${mercado-pago.url-api}") String urlApi,
            @Value("${mercado-pago.test.access-token}") String testAccessToken,
            @Value("${mercado-pago.paths.get-payment-methods}") String pathGetPaymentMethods,
            @Value("${mercado-pago.paths.get-identification-types}") String pathGetIdentificationTypes,
            @Value("${mercado-pago.paths.post-create-customer}") String pathPostCreateClient,
            @Value("${mercado-pago.paths.get-client}") String pathGetClient,
            @Value("${mercado-pago.paths.post-create-preference}") String pathPostCreatePreference,

            @Value("${mercado-pago.preference-config.back-urls.success}") String backUrlSuccess,
            @Value("${mercado-pago.preference-config.back-urls.pending}") String backUrlPending,
            @Value("${mercado-pago.preference-config.back-urls.failure}") String backUrlFailure,
            @Value("${mercado-pago.preference-config.auto-return}") String autoReturn,
            @Value("${mercado-pago.preference-config.expires}") Boolean expires,
            @Value("${mercado-pago.preference-config.statement-descriptor}") String statementDescription,
            @Value("${mercado-pago.preference-config.binary-mode}") Boolean binaryMode
    ){

        this.urlApi = urlApi;
        this.accessToken = testAccessToken;
        this.pathGetPaymentMethods = pathGetPaymentMethods;
        this.pathGetIdentificationTypes = pathGetIdentificationTypes;
        this.pathPostCreateClient = pathPostCreateClient;
        this.pathGetClient = pathGetClient;
        this.pathPostCreatePreference = pathPostCreatePreference;

        this.backUrlSuccess = backUrlSuccess;
        this.backUrlPending = backUrlPending;
        this.backUrlFailure = backUrlFailure;
        this.autoReturn = autoReturn;
        this.expires = expires;
        this.statementDescription = statementDescription;
        this.binaryMode = binaryMode;

    }

    public ResponseEntity<?> createPreference(
            String additional_info
            , String external_reference
            , List<Item> items
            , String email
            , String first_name
            , String last_name
    ) throws HttpMessageNotReadableException, JsonProcessingException {

        BackUrls backUrls = new BackUrls(this.backUrlSuccess,this.backUrlPending,this.backUrlFailure);
        Customer customer = new Customer(email,first_name,last_name);
        Preference preference = new Preference(
                additional_info
                , this.autoReturn
                , backUrls
                , LocalDateTime.now().plusDays(3).toString()
                , this.expires
                , external_reference
                , items
                , this.statementDescription
                , this.binaryMode
                , customer
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        String json = objectMapper.writeValueAsString(preference);


        HttpEntity<String> request = new HttpEntity<String>(json, headers);

        return restTemplate.postForEntity(urlApi+pathPostCreatePreference, request, String.class);
        //JsonNode root = objectMapper.readTree(responseEntityStr.getBody());




    }


}
