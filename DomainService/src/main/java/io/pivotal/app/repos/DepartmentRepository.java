package io.pivotal.app.repos;

import java.util.Collection;

import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.repository.CrudRepository;

import io.pivotal.domain.Department;

public interface DepartmentRepository extends CrudRepository<Department, Integer> {

	/**
	 * TODO: Remove this; o.s.d.repository.CrudRepository.findAll() already exists!
	 */
	@Query("SELECT * FROM /departments")
	Collection<Department> myFindAll();

}
