package io.pivotal.app;

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

import io.pivotal.app.domain.Department;
import io.pivotal.app.domain.Employee;
import io.pivotal.app.repos.DepartmentRepository;
import io.pivotal.app.repos.EmployeeRepository;

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
    return new ReflectionBasedAutoSerializer("io.pivotal.app.domain.*");
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

    departmentRepository.save(new Department("10", "ACCOUNTING"));
    departmentRepository.save(new Department("20", "RESEARCH"));
    departmentRepository.save(new Department("30", "SALES"));
    departmentRepository.save(new Department("40", "OPERATIONS"));
  }

  private void loadEmployeeData(EmployeeRepository employeeRepository) {

    employeeRepository.save(new Employee("7369", "20", "SMITH", "CLERK"));
    employeeRepository.save(new Employee("7370", "10", "APPLES", "MANAGER"));
    employeeRepository.save(new Employee("7371", "10", "WILLIAMS", "SALESMAN"));
    employeeRepository.save(new Employee("7372", "30", "LUCIA", "PRESIDENT"));
    employeeRepository.save(new Employee("7373", "40", "SIENA", "CLERK"));
    employeeRepository.save(new Employee("7374", "10", "LUCAS", "SALESMAN"));
    employeeRepository.save(new Employee("7375", "30", "ROB", "CLERK"));
    employeeRepository.save(new Employee("7376", "20", "ADRIAN", "CLERK"));
    employeeRepository.save(new Employee("7377", "20", "ADAM", "CLERK"));
    employeeRepository.save(new Employee("7378", "20", "SALLY", "MANAGER"));
    employeeRepository.save(new Employee("7379", "10", "FRANK", "CLERK"));
    employeeRepository.save(new Employee("7380", "40", "BLACK", "CLERK"));
    employeeRepository.save(new Employee("7381", "40", "BROWN", "SALESMAN"));
  }
}
