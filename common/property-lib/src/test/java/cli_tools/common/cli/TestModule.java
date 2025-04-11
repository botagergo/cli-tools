package cli_tools.common.cli;

import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
