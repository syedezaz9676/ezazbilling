package com.ezaz.ezbilling.configuration;

import com.ezaz.ezbilling.model.User;
import com.ezaz.ezbilling.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private User user;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            User validUser = usersRepository.findByUsername(username);
            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority(validUser.getRole().get(0)));
                return new org.springframework.security.core.userdetails.User(validUser.getUsername(),validUser.getPassword(), authorities);
        }catch(UsernameNotFoundException e){
            e.printStackTrace();
            throw new UsernameNotFoundException("user not found");
        }
    }
}
