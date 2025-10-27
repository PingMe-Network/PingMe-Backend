package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Admin 10/25/2025
 *
 **/
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("""
              select distinct p
              from Permission p
              join p.roles r
              where upper(r.name) = upper(:roleName)
            """)
    List<Permission> findAllByRoleName(@Param("roleName") String roleName);


}
