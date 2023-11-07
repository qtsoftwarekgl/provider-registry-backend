package com.frpr.config;

import com.frpr.model.ProviderRegistry;
import com.frpr.repo.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ProviderScheduler {


    @Autowired
    ProviderRepository providerRepository;

    @Scheduled(cron = "0 1 1 * * ?")
    public void expiryScheduler() {

        List<ProviderRegistry> providerRegistries = providerRepository.findAll();

        providerRegistries.forEach(r -> {
            if (r.getLicenseExpiryDate() != null) {
                Date currentDate = new Date();
                if(currentDate.after(r.getLicenseExpiryDate())){
                    r.setStatus("INACTIVE");
                    providerRepository.save(r);
                }

            }
        });


    }
}
