package com.elms.dao;

import com.elms.model.Employee;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
    List<Employee> findAll(String search, Integer departmentId, String status);
    Optional<Employee> findById(int id);
    Optional<Employee> findByUserId(int userId);
    Optional<Employee> findByEmployeeCode(String code);
    Optional<Employee> findByEmail(String email);
    int insert(Employee employee, Connection conn);
    boolean update(Employee employee, Connection conn);
    boolean updateStatus(int id, Employee.Status status, Connection conn);
    boolean delete(int id, Connection conn);
    int countTotal();
    int countActive();
}
