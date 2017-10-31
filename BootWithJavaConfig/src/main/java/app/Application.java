package app;

import static org.springframework.data.gemfire.util.ArrayUtils.asArray;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

import io.pivotal.app.repos.DepartmentRepository;
import io.pivotal.app.repos.EmployeeRepository;
import io.pivotal.domain.Department;
import io.pivotal.domain.Employee;

@Configuration
@EnableGemfireRepositories(basePackageClasses = EmployeeRepository.class)
public class Application {

  private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

  public static void main(String[] args) throws IOException {
    new SpringApplication(Application.class).run(args);
  }

  Properties gemfireProperties() {

    Properties gemfireProperties = new Properties();

    gemfireProperties.setProperty("name", "BootWithJavacConfig");
    gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "config"));

    return gemfireProperties;
  }

  @Bean
  ClientCacheFactoryBean gemfireCache(
      @Value("${gemfire.locator.host:localhost}") String locatorHost,
      @Value("${gemfire.locator.port:10334}") int locatorPort) {

    ClientCacheFactoryBean clientCache = new ClientCacheFactoryBean();

    clientCache.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.domain.*"));
    clientCache.setPdxReadSerialized(false);
    clientCache.setProperties(gemfireProperties());

    // Configure GemFire "DEFAULT" Pool
    clientCache.setKeepAlive(false);
    clientCache.setLocators(asArray(new ConnectionEndpoint(locatorHost, locatorPort)));
    clientCache.setPingInterval(TimeUnit.SECONDS.toMillis(5));
    clientCache.setReadTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(20)).intValue());
    clientCache.setRetryAttempts(1);

    return clientCache;
  }

  @Bean("departments")
  ClientRegionFactoryBean<String, Department> departmentsRegion(GemFireCache cache) {

    ClientRegionFactoryBean<String, Department> departmentRegion = new ClientRegionFactoryBean<>();

    departmentRegion.setCache(cache);
    departmentRegion.setClose(false);
    departmentRegion.setShortcut(ClientRegionShortcut.PROXY);

    return departmentRegion;
  }

  @Bean("employees")
  public ClientRegionFactoryBean<Object, Object> employeesRegion(GemFireCache gemfireCache) {

    ClientRegionFactoryBean<Object, Object> employeesRegion = new ClientRegionFactoryBean<>();

    employeesRegion.setCache(gemfireCache);
    employeesRegion.setClose(false);
    employeesRegion.setShortcut(ClientRegionShortcut.PROXY);

    return employeesRegion;
  }

  @Bean
  ApplicationRunner runner(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {

    return args -> {

      Department department = Department.newDepartment(20, "Legal");

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
