package br.com.gabryel.logineer.repository;

import br.com.gabryel.logineer.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> getByEmail(String email);

    boolean existsByEmail(String email);
}