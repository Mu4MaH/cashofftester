package ru.specdep.evolution.cashofftester.service;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.specdep.evolution.cashofftester.entity.CashoffRequest;
import ru.specdep.evolution.cashofftester.entity.CashoffResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*      ***
*
*   Service to send recieved json
*   to Cashoff in signed request
*
      ***     */

@Service
@PropertySource("classpath:app.properties")
public class CashoffRequesterService {

    @Value("${cashoff.service}")
    private String SERVICEID;

    @Value("${cashoff.url}")
    private String url = "http://developer.cashoff.ru/api/json/";

    final private String fullPath = url + "control/parsing-data-r/";

    private String getHmacSHA1(String key, String data) {
        try {
            final String algorithm = "HmacSHA1";
            Mac hasher = Mac.getInstance(algorithm);
            hasher.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm));
            byte[] hash = hasher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash).toUpperCase();
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return null;
        }
    }
    public String coAuth(String request) {
        if (request.isEmpty()) return "fail";
        System.out.println("coAuth path: " + fullPath);
        String timestamp = String.valueOf(System.currentTimeMillis()/1000L);
        final String KEY = "310684cd36b8c8f6731c7909af31fdd9fc8a969b";
        String hashData = SERVICEID + String.valueOf(timestamp) + fullPath + request;
        System.out.println("Signature: " + SERVICEID + ":" + String.valueOf(timestamp) + ":" + getHmacSHA1(KEY, hashData));
        return SERVICEID + ":" + String.valueOf(timestamp) + ":" + getHmacSHA1(KEY, hashData);
    }

    public CashoffResponse requestCashoff(CashoffRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Co-Auth", coAuth(request.toString()));
        headers.add("user-agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
        HttpEntity<CashoffRequest> httpEntity = new HttpEntity<>(request,headers);
        System.out.println("requester: " + fullPath);
        ResponseEntity<CashoffResponse> re = restTemplate.exchange(fullPath, HttpMethod.POST, httpEntity, CashoffResponse.class);
        System.out.println(re.getBody());
        return re.getBody();

//        return restTemplate.postForObject(new URI(fullPath), httpEntity, CashoffResponse.class);
    }

}
