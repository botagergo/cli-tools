package task_manager.property_lib;

import com.google.inject.AbstractModule;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
