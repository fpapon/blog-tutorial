package fr.openobject.blog.tutorial.fusion.persistence;

import fr.openobject.blog.tutorial.fusion.model.CustomerEntity;
import io.yupiik.fusion.framework.api.RuntimeContainer;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.persistence.api.Database;
import io.yupiik.fusion.persistence.impl.DatabaseConfiguration;
import io.yupiik.fusion.persistence.impl.datasource.SimpleDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerManager {

    private static final Logger logger = Logger.getLogger(CustomerManager.class.getName());

    private Database database;

    protected CustomerManager() {
    }

    public CustomerManager(final DatasourceCustomerConfiguration configuration, RuntimeContainer container) {
        DataSource dataSource = new SimpleDataSource(configuration.url(), configuration.username(), configuration.password());
        this.database = Database.of(new DatabaseConfiguration().setDataSource(dataSource), container);
    }

    public CustomerEntity findCustomer(String id) {
        return database.findById(CustomerEntity.class, id);
    }

    public List<CustomerEntity> findAllCustomer() {
        return database.findAll(CustomerEntity.class);
    }
}
