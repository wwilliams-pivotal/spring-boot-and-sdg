package io.pivotal.app.query;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import io.pivotal.app.domain.Employee;

public class QueryEmployeeByKey
{
    private ClientCache cache = null;
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public QueryEmployeeByKey()
    {
        ClientCacheFactory ccf = new ClientCacheFactory();
        ccf.set("cache-xml-file", "config/query-client.xml");
        cache = ccf.create();
    }

    public void run() throws Exception
    {
        Region<Integer,Employee> employees = cache.getRegion("employees");

        String empKey = "7370";
        Employee emp = employees.get(empKey);

        logger.log (Level.INFO, "*************************************************************");
        logger.log (Level.INFO, emp.toString());
        logger.log (Level.INFO, "*************************************************************");
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        QueryEmployeeByKey test = new QueryEmployeeByKey();

        try
        {
            test.run();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
