package io.pivotal.app.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@Region("employees")
@SuppressWarnings("serial")
@AllArgsConstructor(staticName = "newEmployee")
public class Employee implements Serializable {

	@Id
	private String empno;

	private String deptno;

	@NonNull
	private String name;

	private String job;

	@PersistenceConstructor
	public Employee(String empno, String name, String job, String deptno) {
		super();
		this.empno = empno;
		this.deptno = deptno;
		this.name = name;
		this.job = job;
	}

	public Employee() {
	}

	public String getDeptno() {
		return deptno;
	}

	public String getName() {
		return name;
	}

	public String getJob() {
		return job;
	}

	public String getEmpno() {
		return empno;
	}

	public void setName(String name) {

	}

	public void replaceLastName(String lastName) {
		String name = getName();
		name = name.substring(0, name.indexOf(" ")) + " " + lastName;
	}

	@Override
	public String toString() {
		return "Employee [empno=" + getEmpno() + ", name=" + getName() + ", job=" + getJob() + ", deptno=" + getDeptno()
				+ "]";
	}

	public String toCSVFormat() {
		return getEmpno() + "," + getName() + "," + getJob() + "," + getDeptno();
	}
}
