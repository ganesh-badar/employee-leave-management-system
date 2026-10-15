package com.elms.dao;

import com.elms.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentDAO {
    List<Department> findAll();
    Optional<Department> findById(int id);
    Optional<Department> findByCode(String code);
    int insert(Department department);
    boolean update(Department department);
    boolean delete(int id);
}
