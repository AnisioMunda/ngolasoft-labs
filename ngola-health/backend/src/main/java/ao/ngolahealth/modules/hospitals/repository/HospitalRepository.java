package ao.ngolahealth.modules.hospitals.repository;

import ao.ngolahealth.modules.hospitals.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {

    List<Hospital> findByActiveTrue();

    Optional<Hospital> findByCode(String code);

    boolean existsByCode(String code);
}
