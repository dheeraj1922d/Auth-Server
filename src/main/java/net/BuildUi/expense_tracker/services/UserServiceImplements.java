package net.BuildUi.expense_tracker.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.BuildUi.expense_tracker.entities.UserInfo;
import net.BuildUi.expense_tracker.models.UserInfoDto;
import net.BuildUi.expense_tracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserServiceImplements implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserInfo user = userRepository.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found with username "+ username);
        }

        return new CustomUserDetails(user);
    }


    private Boolean signupUser(UserInfoDto userInfoDto){
        UserInfo existUser = userRepository.findByUsername(userInfoDto.getUserName());

        if(existUser != null){
            return false;
        }

        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId , userInfoDto.getUserName() , userInfoDto.getPassword() , new HashSet<>()));

        return true;
    }
}
