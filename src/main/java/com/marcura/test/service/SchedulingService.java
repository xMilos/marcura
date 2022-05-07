package com.marcura.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchedulingService {

    private ExchangeService exchangeService;

    @Autowired
    public SchedulingService(ExchangeService exchangeService){
        this.exchangeService = exchangeService;
    }

    @Scheduled(cron = "0 10 0 * * *", zone = "GMT")
    public void updateExchange(){
        log.info("Scheduling exchange rates...");
        exchangeService.saveOrUpdateExchange();
    }

}
