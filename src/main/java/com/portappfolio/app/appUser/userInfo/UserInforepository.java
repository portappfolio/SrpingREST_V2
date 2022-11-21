package com.portappfolio.app.appUser.userInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInforepository extends JpaRepository<UserInfo,Long> {

    Optional<UserInfo> findByEmail(String email);

}
