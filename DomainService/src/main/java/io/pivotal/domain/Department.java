package io.pivotal.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@Region("departments")
@SuppressWarnings("serial")
@AllArgsConstructor(staticName = "newDepartment")
public class Department implements Serializable {

	@Id
	private int deptno;

	@NonNull
	private String name;

	@PersistenceConstructor
	public Department() {
	}

	@Override
	public String toString() {
		return "Department [deptno=" + getDeptno() + ", name=" + getName() + "]";
	}
}
