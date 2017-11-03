package io.pivotal.app.repos;

import org.springframework.data.repository.CrudRepository;

import io.pivotal.app.domain.Department;

public interface DepartmentRepository extends CrudRepository<Department, String> {

}
