package cli_tools.common.backend;

import cli_tools.common.util.UUIDGenerator;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class).asEagerSingleton();
    }

}
