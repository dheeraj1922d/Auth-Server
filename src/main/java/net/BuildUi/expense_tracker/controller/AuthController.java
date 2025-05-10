package net.BuildUi.expense_tracker.controller;

import net.BuildUi.expense_tracker.auth.SecurityConfig;
import net.BuildUi.expense_tracker.entities.RefreshToken;
import net.BuildUi.expense_tracker.entities.UserInfo;
import net.BuildUi.expense_tracker.models.UserInfoDto;
import net.BuildUi.expense_tracker.requests.AuthRequest;
import net.BuildUi.expense_tracker.response.JwtTokenResponse;
import net.BuildUi.expense_tracker.services.JwtService;
import net.BuildUi.expense_tracker.services.RefreshTokenService;
import net.BuildUi.expense_tracker.services.UserServiceImplements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Component
@RestController
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserServiceImplements userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean isSignUp = userDetailsService.signupUser(userInfoDto);
            if(Boolean.FALSE.equals(isSignUp)){
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUserName());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUserName());
            return new ResponseEntity<>(JwtTokenResponse.builder().accessToken(jwtToken).
                    token(refreshToken.getToken()).build(), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/login")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody AuthRequest authRequestDTO){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authenticate.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            return new ResponseEntity<>(JwtTokenResponse.builder()
                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                    .token(refreshToken.getToken())
                    .build(), HttpStatus.OK);

        } else {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
