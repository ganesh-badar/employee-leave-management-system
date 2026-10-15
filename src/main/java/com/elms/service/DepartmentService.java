package com.elms.service;

import com.elms.dao.DepartmentDAO;
import com.elms.dao.impl.DepartmentDAOImpl;
import com.elms.exception.ValidationException;
import com.elms.model.Department;
import com.elms.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Service managing corporate department records.
 */
public class DepartmentService {

    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    public Optional<Department> getDepartmentById(int id) {
        return departmentDAO.findById(id);
    }

    public Department createDepartment(String code, String name, String description) {
        ValidationUtil.requireNonEmpty(code, "Department code");
        ValidationUtil.requireNonEmpty(name, "Department name");

        String upperCode = code.trim().toUpperCase();
        if (departmentDAO.findByCode(upperCode).isPresent()) {
            throw new ValidationException("code", "Department code '" + upperCode + "' already exists.");
        }

        Department d = new Department();
        d.setDepartmentCode(upperCode);
        d.setDepartmentName(name.trim());
        d.setDescription(description != null ? description.trim() : "");
        int id = departmentDAO.insert(d);
        d.setId(id);
        return d;
    }

    public void updateDepartment(int id, String name, String description) {
        ValidationUtil.requireNonEmpty(name, "Department name");
        Department d = departmentDAO.findById(id)
                .orElseThrow(() -> new ValidationException("Department not found."));
        d.setDepartmentName(name.trim());
        d.setDescription(description != null ? description.trim() : "");
        departmentDAO.update(d);
    }

    public void deleteDepartment(int id) {
        departmentDAO.delete(id);
    }
}
