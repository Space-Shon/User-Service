package ru.headsandhands.userservice.Service;

import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Repository.RepositoryUser;

@Service
public class UserAuthService  {

    private final RepositoryUser repositoryUser;
    public UserAuthService(RepositoryUser repositoryUser){
        this.repositoryUser = repositoryUser;
    }

}