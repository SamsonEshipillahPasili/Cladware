package com.cladware.services;

import com.cladware.entities.CladwareUser;
import com.cladware.repositories.CladwareUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CladwareUserDetailsService implements UserDetailsService {
    @Autowired
    private CladwareUserRepository cladwareUserRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<CladwareUser> optionalUser = this.cladwareUserRepository.findById(s);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new UsernameNotFoundException("No such user!");
        }
    }
}
