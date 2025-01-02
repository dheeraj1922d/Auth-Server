package net.BuildUi.expense_tracker.services;

import net.BuildUi.expense_tracker.entities.RefreshToken;
import net.BuildUi.expense_tracker.entities.UserInfo;
import net.BuildUi.expense_tracker.repositories.RefreshTokenRepository;
import net.BuildUi.expense_tracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private RefreshToken createRefreshToken(String username){
        UserInfo extractedInfo = userRepository.findByUsername(username);
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(extractedInfo)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Token has expired. Please Login again..");
        }
        return token;
    }
}
