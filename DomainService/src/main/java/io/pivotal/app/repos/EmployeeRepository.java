package io.pivotal.app.repos;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import io.pivotal.domain.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

	Collection<Employee> findByDeptno(int deptno);

}
