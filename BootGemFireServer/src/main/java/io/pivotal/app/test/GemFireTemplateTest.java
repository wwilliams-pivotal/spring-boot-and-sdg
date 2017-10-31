package io.pivotal.app.test;

import java.util.Collection;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;

import io.pivotal.app.domain.Employee;

public class GemFireTemplateTest {

  private ConfigurableApplicationContext applicationContext;

  public static void main(String[] args) {

    GemFireTemplateTest test = new GemFireTemplateTest();

    System.out.println("\nStarting Spring Data GemFire Template Test.... \n");

    test.init();
    test.run();

    System.out.println("\nAll done.... ");
  }

  private void init() {
    this.applicationContext = new ClassPathXmlApplicationContext("config/application-context.xml");
  }

  @SuppressWarnings("unchecked")
  private void run() {

    Region<Integer, Employee> employeesRegion = this.applicationContext.getBean("employees", Region.class);

    GemfireTemplate employeesTemplate = new GemfireTemplate(employeesRegion);

    System.out.println("-> template.query() test \n ");

    SelectResults<?> results = employeesTemplate.query("deptno = 40");

    Collection<Employee> employees = (Collection<Employee>) results.asList();

    employees.forEach(System.out::println);

    System.out.println("\n-> template.get(key) test \n ");

    Employee employee = employeesTemplate.get(7373);

    System.out.println(employee.toString());

    System.out.println("\n-> template.find() test \n ");

    SelectResults<Employee> clerkEmployeeResults =
      employeesTemplate.find("SELECT * FROM /employees WHERE job=$1", "CLERK");

    Collection<Employee> clerkEmployees = clerkEmployeeResults.asList();

    clerkEmployees.forEach(System.out::println);
  }
}
