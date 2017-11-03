package io.pivotal.app.domain;

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
	private String deptno;

	@NonNull
	private String name;

	@PersistenceConstructor
	public Department(String deptno, String name) {
		super();
		this.deptno = deptno;
		this.name = name;
	}
	
	public Department() {
	}



//	public static Department newDepartment(int deptno, String name) {
//		return new Department(deptno, name);
//	}
//
	public String getDeptno() {
		return deptno;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Department [deptno=" + getDeptno() + ", name=" + getName() + "]";
	}
}
