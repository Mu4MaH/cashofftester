package ru.specdep.evolution.cashofftester.service;

import org.springframework.stereotype.Service;

@Service
public class IfNull {

    public String checkNull(String field) {
        if (field == null) return "null";
        else return "\"" + field + "\"";
    }

}
