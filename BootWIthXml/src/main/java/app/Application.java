package app;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import io.pivotal.domain.Department;
import io.pivotal.domain.Employee;

@ImportResource({ "classpath:config/application-context.xml" })
@SpringBootApplication
public class Application implements CommandLineRunner {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public static void main(String[] args) throws IOException {
		new SpringApplication(Application.class).run(args);
	}

	@Resource(name = "departmentRegion")
	Region<String, Department> departmentRegion;

	@Resource(name = "clientCache")
	ClientCache clientCache;

	@Override
	public void run(String... strings) throws Exception {

		Department d = new Department(10, "Human Resources");
		departmentRegion.put("10", d);
		d = departmentRegion.get("10");
		System.out.println((d == null) ? "null" : d);

		queryAllEmployees();
	}

	public void queryAllEmployees() throws Exception {
		QueryService queryService = clientCache.getQueryService();

		Query query = queryService.newQuery("SELECT * FROM /employees where deptno = 10");
		logger.log(Level.INFO, "\nExecuting query:\n\t" + query.getQueryString());

		Object result = query.execute();

		Collection<?> collection = ((SelectResults<?>) result).asList();
		logger.log(Level.INFO, String.format("%s Employees in department 10", collection.size()));

		Iterator<?> iter = collection.iterator();

		System.out.println("*************************************************************");
		while (iter.hasNext()) {
			Employee emp = (Employee) iter.next();
			System.out.println(emp.toString());
		}
		System.out.println("*************************************************************");
	}
}
