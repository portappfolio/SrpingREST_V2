package com.portappfolio.app.appUser.userInfo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserInfoService {
    private final UserInforepository userInforepository;

    public Optional<UserInfo> getByEmail(String email){
        return userInforepository.findByEmail(email);
    }

    public UserInfo create(UserInfo userInfo){
        Optional<UserInfo> userInfoOptional = this.getByEmail(userInfo.getEmail());

        if (userInfoOptional.isPresent()){
            return null;
        }

        return userInforepository.save(userInfo);
    }

    public UserInfo update(UserInfo userInfo){
        if (userInfo.getId() == null){
            return null;
        }
        userInfo.setUpdatedAt(LocalDateTime.now());
        return userInforepository.save(userInfo);
    }

    public Boolean delete(UserInfo userInfo){
        if (userInfo.getId() == null){
            return false;
        } else if (userInforepository.findById(userInfo.getId()).isPresent()){
            userInforepository.deleteById(userInfo.getId());
            return true;
        }
        return false;
    }

}
