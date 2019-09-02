package ru.specdep.evolution.cashofftester;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.specdep.evolution.cashofftester.entity.CashoffRequest;
import ru.specdep.evolution.cashofftester.entity.CashoffResponse;
import ru.specdep.evolution.cashofftester.entity.ReqBody;
import ru.specdep.evolution.cashofftester.entity.Session;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class RequestCashoffTest {

    private final String SERVICEID = "infinitum2";

    private final String url = "http://developer.cashoff.ru";

    private final String testRequest = "{\"request\":{\"@method\":\"PARSING_DATA_R\",\"@service\":\"infinitum2\",\"@rid\":\""+ UUID.randomUUID().toString() + "\",\"@session\":{\"@create\":\"true\",\"@institution\":\"tcs\"}}}";

    private String getHmacSHA1(String key, String data) {
        try {
            final String algorithm = "HmacSHA1";
            Mac hasher = Mac.getInstance(algorithm);
            hasher.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm));
            byte[] hash = hasher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String sStringToHMACMD5(String keyString, String s)    {
        String sEncodedString = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
            StringBuffer hash = new StringBuffer();
            for (int i=0; i<bytes.length; i++) {
                String hex = Integer.toHexString(0xFF &  bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            sEncodedString = hash.toString();
        }
        catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {e.printStackTrace();}
        return sEncodedString ;
    }

    @Test
    public void testHash() {
        CashoffRequest cashoffRequest = new CashoffRequest();
        Session session = new Session();
        ReqBody reqBody = new ReqBody();
        session.setCreate("true");
        session.setInstitution("stub");
        reqBody.setMethod("PARSING_DATA_R");
        reqBody.setService("infinitum2");
        reqBody.setSession(session);
        reqBody.setRid(UUID.randomUUID().toString());
        cashoffRequest.setRequestBody(reqBody);
        final String key = "310684cd36b8c8f6731c7909af31fdd9fc8a969b";
        final String serviceId = "infinitum2";
        final String request = "{\"request\":{\"@session\":{\"@create\":\"true\",\"@institution\":\"tcs\"},\"@method\":\"PARSING_DATA_R\",\"@service\":\"infinitum2\",\"@rid\":\""+ UUID.randomUUID().toString() + "\"}}";
        final String path = "/api/json/control/parsing-data-r/";
        final String timestamp = "1566904402";
        String hashData = serviceId + timestamp + path + request;
        System.out.println(getHmacSHA1(hashData, key));

    }

    public String coAuth(String request, String path) {
        final String fullPath = url + path;
        if (request.isEmpty()) return "fail";
        System.out.println("coAuth path: " + fullPath);
        System.out.println("serviceID: " + SERVICEID);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        System.out.println("timestamp: " + timestamp);
        final String KEY = "310684cd36b8c8f6731c7909af31fdd9fc8a969b";
        System.out.println("key: " + KEY);
        System.out.println("path: " + path);
        System.out.println("request: " + request);
        String hashData = SERVICEID + timestamp + path + request;
        System.out.println("hashdata: " + hashData);
        System.out.println("Co-Auth: " + SERVICEID + ":" + timestamp + ":" + getHmacSHA1(KEY, hashData));
        return SERVICEID + ":" + timestamp + ":" + getHmacSHA1(KEY, hashData);
    }

    @Test
    public void requestCashoff() throws URISyntaxException {
        final String url_ass = "/api/json/control/parsing-data-r/";
        final String fullPath = url + url_ass;
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
        headers.set("Co-Auth", coAuth(cashoffRequest.toString(), url_ass));
        HttpEntity<String> httpEntity = new HttpEntity(cashoffRequest, headers);
        System.out.println("URI: " + new URI(fullPath));
        try {
            ResponseEntity<CashoffResponse> re = restTemplate.exchange(new URI(fullPath), HttpMethod.POST, httpEntity, CashoffResponse.class);
        }
        catch (HttpStatusCodeException e) {
            String errorBody = e.getResponseBodyAsString();
            System.out.println( errorBody);
        }
    }

    @Test
    public void requestCashoffOldSchool() throws IOException {
        final String url_ass = "/api/json/control/parsing-data-r/";
        final String fullPath = url + url_ass;
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
        URL url = new URL(fullPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Co-Auth", coAuth(cashoffRequest.toString(), url_ass));
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100");
        try (OutputStream out = conn.getOutputStream()) {
            out.write(cashoffRequest.toString().getBytes(StandardCharsets.UTF_8));
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

    private String getNewSession() {
        return new String("");
    }


    @Test
    public void getPaymentProductsTest() {
        final String url_ass = "/api/json/control/get-payment-products/";
        final String fullPath = url + url_ass;
        final String session  = getNewSession();
        final CashoffRequest cashoffRequest = new CashoffRequest();

    }
}
