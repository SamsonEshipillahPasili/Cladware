package com.cladware;

import com.cladware.entities.CladwareUser;
import com.cladware.repositories.CladwareUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAccountsLoader implements CommandLineRunner{
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private CladwareUserRepository cladwareUserRepository;

    @Override
    public void run(String... args) throws Exception {
        // create a sample user and save them to the DB
        CladwareUser cladwareUser = new CladwareUser();
        cladwareUser.setEmail("sam@gmail.com");
        cladwareUser.setFullname("Eshipillah Pasili Samson");
        cladwareUser.setGender("Male");
        cladwareUser.setRole("ADMIN");
        cladwareUser.setPassword(this.passwordEncoder.encode("password"));

        CladwareUser cladwareUser2 = new CladwareUser();
        cladwareUser2.setEmail("melissa@gmail.com");
        cladwareUser2.setFullname("Eshipillah Pasili Samson");
        cladwareUser2.setGender("Male");
        cladwareUser2.setRole("USER");
        cladwareUser2.setPassword(this.passwordEncoder.encode("password"));

        cladwareUserRepository.save(cladwareUser);
        cladwareUserRepository.save(cladwareUser2);
    }
}
