package ru.specdep.evolution.cashofftester.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CashoffRequest {

    @JsonProperty("request")
    private ReqBody requestBody;

    public ReqBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(ReqBody requestBody) {
        this.requestBody = requestBody;
    }

    public CashoffRequest() {
    }

    public CashoffRequest(ReqBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "{request:" + requestBody.toString() + " }";
    }
}
