package app;

import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.CacheServerConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableLocator;
import org.springframework.data.gemfire.config.annotation.EnableManager;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

import io.pivotal.app.repos.DepartmentRepository;
import io.pivotal.app.repos.EmployeeRepository;
import io.pivotal.domain.Department;
import io.pivotal.domain.Employee;

@CacheServerApplication(name = "SpringBootGemFireCacheServer")
@EnableEntityDefinedRegions(basePackageClasses = Employee.class)
@EnableGemfireRepositories(basePackageClasses = EmployeeRepository.class)
@EnableLocator
@EnableManager
@EnablePdx(serializerBeanName = "autoSerializer")
public class GemFireServer {

  public static void main(String[] args) {
    SpringApplication.run(GemFireServer.class, args);
  }

  @Bean
  ReflectionBasedAutoSerializer autoSerializer() {
    return new ReflectionBasedAutoSerializer("io.pivotal.domain.*");
  }

  @Bean
  CacheServerConfigurer cacheServerPortConfigurer(@Value("${gemfire.cache.server.port:40404}") int port) {
    return (beanName, cacheServerFactoryBean) -> cacheServerFactoryBean.setPort(port);
  }

  @Bean
  ApplicationRunner runner(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {

    return args -> {
      loadDepartmentData(departmentRepository);
      loadEmployeeData(employeeRepository);
    };
  }

  private void loadDepartmentData(DepartmentRepository departmentRepository) {

    departmentRepository.save(Department.newDepartment(10, "ACCOUNTING"));
    departmentRepository.save(Department.newDepartment(20, "RESEARCH"));
    departmentRepository.save(Department.newDepartment(30, "SALES"));
    departmentRepository.save(Department.newDepartment(40, "OPERATIONS"));
  }

  private void loadEmployeeData(EmployeeRepository employeeRepository) {

    employeeRepository.save(Employee.newEmployee(7369, 20, "SMITH", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7370, 10, "APPLES", "MANAGER"));
    employeeRepository.save(Employee.newEmployee(7371, 10, "WILLIAMS", "SALESMAN"));
    employeeRepository.save(Employee.newEmployee(7372, 30, "LUCIA", "PRESIDENT"));
    employeeRepository.save(Employee.newEmployee(7373, 40, "SIENA", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7374, 10, "LUCAS", "SALESMAN"));
    employeeRepository.save(Employee.newEmployee(7375, 30, "ROB", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7376, 20, "ADRIAN", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7377, 20, "ADAM", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7378, 20, "SALLY", "MANAGER"));
    employeeRepository.save(Employee.newEmployee(7379, 10, "FRANK", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7380, 40, "BLACK", "CLERK"));
    employeeRepository.save(Employee.newEmployee(7381, 40, "BROWN", "SALESMAN"));
  }
}
