package ru.headsandhands.userservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.headsandhands.userservice.Model.User;

import java.util.Optional;

public interface RepositoryUser extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

}
