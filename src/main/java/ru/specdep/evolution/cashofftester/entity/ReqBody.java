package ru.specdep.evolution.cashofftester.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.specdep.evolution.cashofftester.service.IfNull;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqBody {

    IfNull ifNull = new IfNull();

    @JsonProperty("session")
    private Session session;

    @JsonProperty("@method")
    private String method = "asdad";

    @JsonProperty("@service")
    private String service = "wefwef";

    @JsonProperty("@rid")
    private String rid = "wfgfew";

    public ReqBody() {
    }

    public ReqBody(Session session, String method, String service, String rid) {
        this.session = session;
        this.method = method;
        this.service = service;
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "{\"session\":" + session.toString() + ",\"@method\":" + ifNull.checkNull(method) + ",\"@service\":\"" + service + "\",\"@rid\":\"" + rid + "\"}";
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
}
