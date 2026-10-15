package com.elms.dao;

import com.elms.model.LeaveType;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeDAO {
    List<LeaveType> findAll();
    Optional<LeaveType> findById(int id);
    Optional<LeaveType> findByCode(String code);
}
