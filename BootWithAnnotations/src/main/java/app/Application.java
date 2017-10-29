package app;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

import io.pivotal.domain.Department;
import io.pivotal.domain.Employee;

@Configuration
@SpringBootApplication
public class Application implements CommandLineRunner {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	  @Bean
	  ClientCacheConfigurer clientCachePoolPortConfigurer(
	          @Value("${gemfire.cache.locator.host:localhost}") String cacheLocatorHost,
	          @Value("${gemfire.cache.locator.port:10334}") int cacheLocatorPort) {

	      return (beanName, clientCacheFactoryBean) -> {
	          clientCacheFactoryBean.setLocators(Collections.singletonList(
	              new ConnectionEndpoint(cacheLocatorHost, cacheLocatorPort)));
	      };
	  }

	@Bean
	ClientCacheFactoryBean clientCache() {
		ClientCacheFactoryBean clientCache = new ClientCacheFactoryBean();
		clientCache.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.domain.*"));
		clientCache.setPdxReadSerialized(false);
		clientCache.setClose(true);
		clientCache.setProperties(gemfireProperties());
		clientCache.setPool(gemfirePool("localhost", 10334));
		return clientCache;
	}

	@Bean(name = "clientPool")
	Pool gemfirePool(@Value("${gemfire.client.locator.host:localhost}") String host,
			@Value("${gemfire.client.locator.port:10334}") int port) {
		PoolFactoryBean pool = new PoolFactoryBean();

		pool.setKeepAlive(false);
		pool.setPingInterval(TimeUnit.SECONDS.toMillis(5));
		pool.setReadTimeout((int) TimeUnit.SECONDS.toMillis(20));
		pool.setRetryAttempts(1);
		pool.addLocators(new ConnectionEndpoint(host, port));
		pool.setName(GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME);
		return pool.getPool();
	}

	@Bean
	Properties gemfireProperties() {
		Properties gemfireProperties = new Properties();
		gemfireProperties.setProperty("name", "SpringGemFireApplication");
		gemfireProperties.setProperty("mcast-port", "0");
		gemfireProperties.setProperty("log-level", "config");
		return gemfireProperties;
	}

	@Bean
	ClientRegionFactoryBean<String, Department> departmentRegion(final GemFireCache cache,
			@Qualifier("clientPool") Pool gemfirePool) {
		ClientRegionFactoryBean<String, Department> departmentRegion = new ClientRegionFactoryBean<>();
		departmentRegion.setCache(cache);
		departmentRegion.setClose(false);
		departmentRegion.setName("departments");
		departmentRegion.setPersistent(false);
		departmentRegion.setPoolName(gemfirePool("localhost", 10334).getName());
		departmentRegion.setShortcut(ClientRegionShortcut.PROXY);
		return departmentRegion;
	}

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
