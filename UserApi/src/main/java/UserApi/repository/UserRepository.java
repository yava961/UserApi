package UserApi.repository;


import UserApi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;


public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT * FROM user WHERE birth_date >= :startDate AND birth_date <= :endDate ORDER BY ?#{#pageable}",
            countQuery = "SELECT count(*) FROM user WHERE birth_date >= :startDate AND birth_date <= :endDate",
            nativeQuery = true)
    Page<User> findByBirthDateBetween(@Param("startDate") LocalDate localDateFrom, @Param("endDate") LocalDate localDateTo, Pageable pageable);
}
