package com.transitx.repository;

import com.transitx.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByRouteCode(String routeCode);
    Boolean existsByRouteCode(String routeCode);

    @Query("SELECT r FROM Route r WHERE " +
           "LOWER(r.routeName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.routeCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.startPoint) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.endPoint) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Route> searchRoutes(@Param("query") String query);
}
