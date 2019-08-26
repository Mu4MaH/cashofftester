package ru.specdep.evolution.cashofftester;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.specdep.evolution.cashofftester.entity.CashoffRequest;
import ru.specdep.evolution.cashofftester.entity.CashoffResponse;
import ru.specdep.evolution.cashofftester.entity.ReqBody;
import ru.specdep.evolution.cashofftester.entity.Session;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class RequestCashoffTest {

    private final String SERVICEID = "infinitum2";

    private final String url = "http://developer.cashoff.ru";

    private final String url_ass = "/api/json/control/parsing-data-r/";

    private final String fullPath = url + url_ass;

    private final String testRequest = "{\"request\": {\"@method\": \"PARSING_DATA_R\",\"@service\": \"infinitum2\",\"@rid\": \""+ UUID.randomUUID().toString() + "\",\"@session\" : {\"@create\": \"true\",\"@institution\": \"tcs\"}}}";

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
        System.out.println("serviceID: infinitum2");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        System.out.println("timestamp: " + timestamp);
        final String KEY = "310684cd36b8c8f6731c7909af31fdd9fc8a969b";
        System.out.println("key: " + KEY);
        System.out.println("path: " + url_ass);
        System.out.println("request: " + testRequest);
        String hashData = SERVICEID + String.valueOf(timestamp) + url_ass + request;
        System.out.println("hashdata: " + hashData);
        System.out.println("Co-Auth: " + SERVICEID + ":" + String.valueOf(timestamp) + ":" + getHmacSHA1(KEY, hashData));
        return SERVICEID + ":" + String.valueOf(timestamp) + ":" + getHmacSHA1(KEY, hashData);
    }

    @Test
    public void requestCashoff() throws URISyntaxException {
        CashoffRequest cashoffRequest = new CashoffRequest();
        CashoffResponse cashoffResponse = new CashoffResponse();
        Session session = new Session();
        ReqBody reqBody = new ReqBody();
        session.setCreate("true");
        session.setInstitution("stub");
        reqBody.setMethod("PARSING_DATA_R");
        reqBody.setService("infinitum2");
        reqBody.setSession(session);
        reqBody.setRid(UUID.randomUUID().toString());
        cashoffRequest.setRequestBody(reqBody);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        headers.set("Co-Auth", coAuth(cashoffRequest.toString()));
        headers.add("User-Agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
        HttpEntity<String> httpEntity = new HttpEntity(cashoffRequest, headers);
        System.out.println("URI: " + new URI(fullPath));
        ResponseEntity<CashoffResponse> re = restTemplate.exchange(new URI(fullPath), HttpMethod.POST, httpEntity, CashoffResponse.class);
        System.out.println(re.getBody());
    }

    @Test
    public void requestCashoffOldSchool() throws IOException {
        URL url = new URL(fullPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Co-Auth", coAuth(testRequest));
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100");
        try (OutputStream out = conn.getOutputStream()) {
            out.write(testRequest.getBytes(StandardCharsets.UTF_8));
        }
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            System.out.println(content.toString());
        } catch (final Exception ex) {
            ex.printStackTrace();
            System.out.println("!!!!!");;
        }
    }
}
