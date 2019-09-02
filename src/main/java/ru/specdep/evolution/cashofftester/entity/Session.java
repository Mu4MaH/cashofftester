package ru.specdep.evolution.cashofftester.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.specdep.evolution.cashofftester.service.IfNull;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Session {

    IfNull ifNull = new IfNull();

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@create")
    private String create;

    @JsonProperty("@institution")
    private String institution;

    public Session(String id, String create, String institution) {
        this.id = id;
        this.create = create;
        this.institution = institution;
    }

    public Session() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public String toString() {
        return "{\"@id\":" + ifNull.checkNull(id) + ",\"@create\":\"" + create + "\",\"@institution\":\"" + institution + "\"}";
    }
}
