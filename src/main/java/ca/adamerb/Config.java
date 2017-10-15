package ca.adamerb;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import spark.utils.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

public class Config {

    public static String loadResource(String resourceName) {
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> loadProperties() {
        File fp = new File("application.properties");
        if(!fp.exists()) throw new RuntimeException("Can't find an instance of application.properties");

        Properties props = new Properties();
        try {
            HashMap<String, String> map = new HashMap<>();
            props.load(new FileInputStream(fp));
            props.keySet().forEach(key -> {
                if(key instanceof String) {
                    map.put((String) key, props.getProperty((String) key));
                }
            });
            return map;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties.", e);
        }
    }

    public static DataSource configureMySqlDatasource(Map<String, String> properties) {
        String url = properties.get("db.url");
        String user = properties.get("db.user");
        String password = properties.get("db.password");

        Assert.notNull(url);
        Assert.notNull(user);

        PoolProperties p = new PoolProperties();
        p.setUrl(url);
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(user);
        p.setPassword(password);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(3);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;" +
                "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);
        return datasource;
    }
}
