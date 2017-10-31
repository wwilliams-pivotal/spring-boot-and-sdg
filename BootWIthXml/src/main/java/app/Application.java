package app;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import io.pivotal.app.repos.DepartmentRepository;
import io.pivotal.app.repos.EmployeeRepository;
import io.pivotal.domain.Department;
import io.pivotal.domain.Employee;

@Configuration
@ImportResource("classpath:config/application-context.xml")
public class Application {

	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public static void main(String[] args) throws IOException {
		new SpringApplication(Application.class).run(args);
	}

	@Bean
	ApplicationRunner runner(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {

		return args -> {

			Department department = Department.newDepartment(30, "Marketing");

			department = departmentRepository.save(department);
			department = departmentRepository.findById(department.getDeptno()).orElse(null);

			System.out.printf("Department is [%s]%n", department);

			queryAllEmployeesByDepartment(employeeRepository, department.getDeptno());

		};
	}

	private void queryAllEmployeesByDepartment(EmployeeRepository employeeRepository, int departmentNumber)
			throws Exception {

		Collection<Employee> employeesInDepartment = employeeRepository.findByDeptno(departmentNumber);

		this.logger.info(() -> String.format("There are %d Employees in Department %d",
			employeesInDepartment.size(), departmentNumber));

		this.logger.info("*************************************************************");

		employeesInDepartment.forEach(System.out::println);

		this.logger.info("*************************************************************");
	}
}
