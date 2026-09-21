package ao.ngolahealth.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ao.ngolahealth.modules.auth.entity.User;
 
public interface UserRepository extends JpaRepository<User,UUID> {
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
