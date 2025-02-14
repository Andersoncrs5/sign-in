package com.signin.signin.Repositories;

import com.signin.signin.Models.UserModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {
    @Query(value = "SELECT * FROM USERS WHERE ID = :ID", nativeQuery = true)
    UserModel findUser(Long id);

    @Query(value = "SELECT * FROM USERS WHERE EMAIL = :EMAIL", nativeQuery = true)
    UserModel findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM USERS U WHERE U.ID = :id", nativeQuery = true)
    void deleteUser(Long id);

    @Query(value = "SELECT * FROM USERS U WHERE U.ID = :id LOCK IN SHARE MODE;", nativeQuery = true)
    UserModel findUserToUpdate(Long id);
}
