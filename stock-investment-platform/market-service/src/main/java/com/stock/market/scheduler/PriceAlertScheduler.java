package com.stock.market.scheduler;

import com.stock.market.service.PriceAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceAlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertScheduler.class);

    @Autowired
    private PriceAlertService priceAlertService;

    @Scheduled(fixedRate = 10000)
    public void checkAlerts() {
        log.debug("Running price alert check...");
        priceAlertService.checkAndTriggerAlerts();
    }
}
