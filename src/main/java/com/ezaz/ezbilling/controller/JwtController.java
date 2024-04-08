package com.ezaz.ezbilling.controller;

import com.ezaz.ezbilling.configuration.MyUserDetailsService;
import com.ezaz.ezbilling.helper.JwtUtil;
import com.ezaz.ezbilling.model.JwtRequest;
import com.ezaz.ezbilling.model.JwtResponse;
import com.ezaz.ezbilling.model.JwtUser;
import com.ezaz.ezbilling.model.User;
import com.ezaz.ezbilling.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
     private MyUserDetailsService myUserDetailsService;

    private final UsersRepository usersRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public JwtController(AuthenticationManager authenticationManager, UsersRepository usersRepository, UsersRepository usersRepository1) {
        this.usersRepository = usersRepository1;
    }

    @CrossOrigin(origins = "http://localhost:8081/")
    @RequestMapping(value="/token", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        System.out.println(jwtRequest);
        try{
            this.authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),jwtRequest.getPassword()));

        }catch (UsernameNotFoundException e){
            e.printStackTrace();
            throw new Exception("User not Found");

        }catch (BadCredentialsException e){
            e.printStackTrace();
            throw new Exception("Worng Password");
        }

        UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token = this.jwtUtil.generateToken(userDetails);
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        JwtUser user = new JwtUser();
        User userDetailsByID =usersRepository.findByUsername(userDetails.getUsername());
        user.setId(userDetailsByID.getId());
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getAuthorities());
        user.setPrefix(userDetailsByID.getPrefix());
        user.setFirmName(userDetailsByID.getFirmName());
        user.setAddress(userDetailsByID.getAddress());
        user.setGstNo(userDetailsByID.getGstNo());
        user.setContact(userDetailsByID.getContact());
        user.setState(userDetailsByID.getState());


        jwtResponse.setUser(user);
        return ResponseEntity.ok(jwtResponse);
    }
}
