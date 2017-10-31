package app;

import static org.springframework.data.gemfire.util.ArrayUtils.asArray;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

import io.pivotal.app.repos.DepartmentRepository;
import io.pivotal.app.repos.EmployeeRepository;
import io.pivotal.domain.Department;
import io.pivotal.domain.Employee;

@ClientCacheApplication(name = "BootWithAnnotations", pingInterval = 5000, readTimeout = 20000, retryAttempts = 1)
@EnableEntityDefinedRegions(basePackageClasses = Employee.class)
@EnableGemfireRepositories(basePackageClasses = EmployeeRepository.class)
@EnablePdx(serializerBeanName = "autoSerializer")
public class Application {

  private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

  public static void main(String[] args) throws IOException {
    new SpringApplication(Application.class).run(args);
  }

  @Bean
  ReflectionBasedAutoSerializer autoSerializer() {
    return new ReflectionBasedAutoSerializer("io.pivotal.domain.*");
  }

  @Bean
  ClientCacheConfigurer clientCacheDefaultPoolPortConfigurer(
      @Value("${gemfire.locator.host:localhost}") String locatorHost,
      @Value("${gemfire.locator.port:10334}") int locatorPort) {

    return (beanName, clientCacheFactoryBean) ->
      clientCacheFactoryBean.setLocators(asArray(new ConnectionEndpoint(locatorHost, locatorPort)));
  }

  @Bean
  ApplicationRunner runner(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {

    return args -> {

      Department department = Department.newDepartment(10, "Human Resources");

      department = departmentRepository.save(department);
      department = departmentRepository.findById(department.getDeptno()).orElse(null);

      System.out.printf("Department is [%s]%n", department);

      queryAllEmployeesByDepartment(employeeRepository, department.getDeptno());

    };
  }

  private void queryAllEmployeesByDepartment(EmployeeRepository employeeRepository, int departmentNumber) throws Exception {

    Collection<Employee> employeesInDepartment = employeeRepository.findByDeptno(departmentNumber);

    this.logger.info(() -> String.format("There are %d Employees in Department %d",
      employeesInDepartment.size(), departmentNumber));

    this.logger.info("*************************************************************");

    employeesInDepartment.forEach(System.out::println);

    this.logger.info("*************************************************************");
  }
}
