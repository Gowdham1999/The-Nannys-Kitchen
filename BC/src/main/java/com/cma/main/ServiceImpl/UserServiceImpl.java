package com.cma.main.ServiceImpl;

import com.cma.main.Constants.Constants;
import com.cma.main.DAO.UserDAO;
import com.cma.main.JWT.CustomerDetailsService;
import com.cma.main.JWT.JwtFilter;
import com.cma.main.JWT.JwtUtils;
import com.cma.main.POJO.User;
import com.cma.main.Service.UserService;
import com.cma.main.Utils.CafeUtils;
import com.cma.main.Utils.EmailUtils;
import com.cma.main.Wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerDetailsService customerDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    //    SignUp Functionality
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {

        log.info("Inside signUp()", requestMap);
        try {
            if (validateRequestMap(requestMap)) {
                User user = userDAO.findFirstByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDAO.save(userObjectFromMap(requestMap));
                    return CafeUtils.getResponse("Signup Successful", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponse("Email already exists!", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateRequestMap(Map<String, String> requestMap) {
        if (validateKey(requestMap) && validateValueOfKey(requestMap)) {
            return true;
        }
        return false;
    }

    private boolean validateKey(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password") && validateValueOfKey(requestMap)) {
            return true;
        }
        return false;
    }

    private boolean validateValueOfKey(Map<String, String> requestMap) {
        if ((requestMap.get("name") != null && requestMap.get("name") != "") && (requestMap.get("contactNumber") != null && requestMap.get("contactNumber") != "") && (requestMap.get("email") != null && requestMap.get("email") != "") && (requestMap.get("password") != null && requestMap.get("password") != "")) {
            return true;
        }
        return false;
    }

    private User userObjectFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("False");
        user.setRole("User");

        return user;
    }


    //    Login Functionality
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside Login");
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (auth.isAuthenticated()) {
//                In the below we are checking whether the admin has approved the user
                if (customerDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtils.generateToken(customerDetailsService.getUserDetails().getEmail(), customerDetailsService.getUserDetails().getRole()) + "\"}", HttpStatus.OK);
                } else {
//                    If the admin yet to approve the user, then
                    return new ResponseEntity<String>("{\"message\":\"" + "Wait for Admin's Approval. Thanks!" + "\"}", HttpStatus.OK);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return CafeUtils.getResponse("\"" + Constants.BAD_CREDENTIALS + "\"", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        log.info("Inside getAllUsers in UserServiceImpl:");
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDAO.getAllUsers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        log.info("Inside updateStatus in UserServiceImpl:--->", requestMap);
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optionalUserData = userDAO.findById(Integer.parseInt(requestMap.get("id")));
                if (!optionalUserData.isEmpty()) {
                    userDAO.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
//                    Sending Mails to admins if admin approves or rejects the user
                    sendMailsToAdmin(requestMap.get("status"), optionalUserData.get().getEmail(), userDAO.getAllAdmins());
                    return CafeUtils.getResponse(Constants.USER_UPDATE_SUCCESS, HttpStatus.OK);
                } else {
                    return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponse(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailsToAdmin(String status, String email, List<String> allAdmins) {
        allAdmins.remove(jwtFilter.getCurrentUserName());

        if (status.equalsIgnoreCase("true") && status != null) {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUserName(), "Admin has enabled a user!", "User " + email + " has been enabled by Admin: " + jwtFilter.getCurrentUserName() + "", allAdmins);
        } else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUserName(), "Admin has disabled a user!", "User " + email + " has been disabled by Admin: " + jwtFilter.getCurrentUserName() + "", allAdmins);
        }

    }

    //    With the checkToken we are going to validate the user.
    @Override
    public ResponseEntity<String> checkToken() {
        return new ResponseEntity<>("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObject = userDAO.findFirstByEmail(jwtFilter.getCurrentUserName());  //Here current username is the email of the user.

            if (!userObject.equals(null)) {
                if (userObject.getPassword().equals(requestMap.get("oldPassword"))) {
                    userObject.setPassword(requestMap.get("newPassword"));
                    userDAO.save(userObject);
                    return CafeUtils.getResponse(Constants.PASSWORD_UPDATE_SUCCESSFUL, HttpStatus.OK);

                } else {
                    return CafeUtils.getResponse(Constants.INCORRECT_PASSWORD, HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User userObject = userDAO.findFirstByEmail(requestMap.get("emailID"));  //Here current username is the email of the user.

            if (!userObject.equals(null) && !Strings.isNullOrEmpty(userObject.getEmail()))
                emailUtils.sendPassword(requestMap.get("emailID"), "Nanny's Kitchen - Reset Password", userObject.getPassword());

            return CafeUtils.getResponse(Constants.FORGOT_PASS_EMAIL_SUCCESS, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
