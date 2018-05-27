package br.com.gabryel.logineer.repository;

import br.com.gabryel.logineer.entities.Phone;
import br.com.gabryel.logineer.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, String> {
    List<Phone> findByUser(User user);
}