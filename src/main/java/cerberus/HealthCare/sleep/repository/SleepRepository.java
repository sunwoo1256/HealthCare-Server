package cerberus.HealthCare.sleep.repository;

import cerberus.HealthCare.sleep.entity.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepRepository extends JpaRepository<SleepLog, Long> {

}
