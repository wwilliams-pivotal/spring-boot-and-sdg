package io.pivotal.app.repos;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import io.pivotal.app.domain.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, String> {

	Collection<Employee> findByDeptno(String deptno);

}
