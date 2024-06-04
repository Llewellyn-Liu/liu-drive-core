package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByUserId(String userId);

    int deleteUserByUserId(String userId);


    @Modifying
    @Query(value = "update DR_USER as u set u.userId = ?1, u.password = ?2 where u.username = ?3", nativeQuery = true)
    int setNewInfoToUsername( String username, String password, String userId);
}
