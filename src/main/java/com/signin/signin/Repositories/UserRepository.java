package com.signin.signin.Repositories;

import com.signin.signin.Models.UserModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM USERS U WHERE U.ID = :id", nativeQuery = true)
    void deleteUser(Long id);
}
