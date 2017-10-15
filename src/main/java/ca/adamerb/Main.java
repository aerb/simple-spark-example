package ca.adamerb;

import com.hubspot.jinjava.Jinjava;
import org.apache.tomcat.jdbc.pool.DataSource;
import spark.Spark;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final String template = Config.loadResource("test.html");
    private static final Jinjava jinjava = new Jinjava();

    public static void main(String[] args) {
        System.out.println("Starting application. ");
        Map<String, String> properties = Config.loadProperties();
        DataSource dataSource = Config.configureMySqlDatasource(properties);

        Spark.get("/hello", (req, res) -> {

            try (Connection connection = dataSource.getConnection()) {
                ResultSet rs = connection.prepareStatement("SELECT 1;").executeQuery();
                rs.first();

                Map<String, Object> context = new HashMap<>();
                context.put("result", rs.getString(1));

                return jinjava.render(template, context);
            }
        });
    }
}
