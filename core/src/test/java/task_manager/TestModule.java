package task_manager;

import com.google.inject.AbstractModule;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.UUIDGenerator;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
