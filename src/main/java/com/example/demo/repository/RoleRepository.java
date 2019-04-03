package com.example.demo.repository;

import com.example.demo.data.user.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByDescriptionAndAndName(String description, String name);
}
