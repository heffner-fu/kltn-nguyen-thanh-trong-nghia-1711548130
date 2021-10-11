package com.khoaluan.admin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.khoaluan.common.model.Role;

@Repository 
public interface RoleRepository extends CrudRepository<Role, Integer> {

}
