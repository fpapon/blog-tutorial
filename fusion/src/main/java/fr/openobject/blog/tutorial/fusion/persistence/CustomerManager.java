package fr.openobject.blog.tutorial.fusion.persistence;

import fr.openobject.blog.tutorial.fusion.model.CustomerEntity;
import io.yupiik.fusion.framework.api.scope.ApplicationScoped;
import io.yupiik.fusion.persistence.api.Database;
import io.yupiik.fusion.persistence.impl.DatabaseConfiguration;
import io.yupiik.fusion.persistence.impl.datasource.SimpleDataSource;

import javax.sql.DataSource;

@ApplicationScoped
public class CustomerManager {

    private Database database;

    protected CustomerManager() {
    }

    public CustomerManager(final DatasourceCustomerConfiguration configuration) {
        DataSource dataSource = new SimpleDataSource(configuration.url(), configuration.username(), configuration.password());
        this.database = Database.of(new DatabaseConfiguration().setDataSource(dataSource));
    }

    public CustomerEntity findCustomer(String id) {
        return database.findById(CustomerEntity.class, id);
    }
}
