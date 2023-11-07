package com.frpr.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.frpr.model.User;
import com.frpr.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.frpr.repo.ProviderRepository;
import com.frpr.model.ProviderRegistry;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class ProviderExpireDate {
    @Autowired
    ProviderRepository repository;

    @Autowired
    CustomerRepository customerRepository;
    //@Scheduled(cron = "@midnight")

    @Scheduled(cron = "0 5 0 * * *")
    public void updateLicenceStatus() {

        List<ProviderRegistry> providers = repository.findAll();
        Long updatedrows = (long) 0;
        for (ProviderRegistry prov : providers) {
            System.out.println(prov.get_id());

            Date exp = prov.getLicenseExpiryDate();
            Date now = new Date();

            if (now.after(exp)) {
                if (!prov.getLicense_status().equals("Expired")) {

                    prov.setLicense_status("Expired");
                    prov.setStatus("INACTIVE");
                    repository.save(prov);
                    updatedrows++;
                }

            }

        }
        System.out.println("updated rows:" + updatedrows + "  at: " + new Date());
        lastPasswordRestJob();
    }

    private void lastPasswordRestJob() {
        List<User> users = customerRepository.findAllByLastPasswordResetDateLessThanEqualAndIsRequiredToResetPassword(dateBefore30Days(), false);

        for (User u : users) {
            u.setIsRequiredToResetPassword(true);
        }
        customerRepository.saveAll(users);

    }

    private Date dateBefore30Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
