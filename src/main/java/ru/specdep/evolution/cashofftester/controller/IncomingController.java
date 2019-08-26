package ru.specdep.evolution.cashofftester.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.specdep.evolution.cashofftester.entity.CashoffRequest;
import ru.specdep.evolution.cashofftester.entity.CashoffResponse;
import ru.specdep.evolution.cashofftester.service.CashoffRequesterService;

import java.net.URISyntaxException;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(path = "/cashoff")
public class IncomingController {

    @Autowired
    CashoffRequesterService cashoffRequesterService;

    @PostMapping(path = "/request", consumes = APPLICATION_JSON_UTF8_VALUE)
    public CashoffResponse requester(@RequestBody CashoffRequest cashoffRequest) throws URISyntaxException {
        return cashoffRequesterService.requestCashoff(cashoffRequest);
    }

}
