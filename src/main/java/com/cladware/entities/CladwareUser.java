package com.cladware.entities;

import com.cladware.dto.Cart;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
public class CladwareUser implements UserDetails{
    @Column
    private String fullname;
    @Column
    @Id
    private String email;
    @Column
    private String password;
    @Column
    private String gender;
    @Column
    private String role;
    @Lob
    @Column
    private Cart cart = new Cart();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role.equals("USER")){
            return AuthorityUtils.createAuthorityList("USER");
        }else if(role.equals("ADMIN")){
            return AuthorityUtils.createAuthorityList("ADMIN");
        }else{
            return AuthorityUtils.createAuthorityList("UNKNOWN");
        }
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
