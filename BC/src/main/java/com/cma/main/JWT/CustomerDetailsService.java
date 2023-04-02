package com.cma.main.JWT;

import com.cma.main.DAO.UserDAO;
import com.cma.main.POJO.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
@Slf4j
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    UserDAO userDAO;

    private User userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Entering into loadUserByUserName - [package com.cma.main.JWT/CustomerDetailsService]", username);
        userDetails = userDAO.findFirstByEmail(username);//Here in Username we will be passing the email id. We can also name username as email

        if (!Objects.isNull(userDetails)) {
            log.info("Returning Spring Security User with the details fetched from userDetails - [package com.cma.main.JWT/CustomerDetailsService]");
            return new org.springframework.security.core.userdetails.User(userDetails.getEmail(), userDetails.getPassword(), new ArrayList<>());
        }

        throw new UsernameNotFoundException("User Not Found!!!");
    }

    public User getUserDetails() {

        User user = userDetails;
        user.setPassword("********");
        return user;
    }
}
