package cli_tools.common.cli;

import com.google.inject.AbstractModule;
import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.cli.tokenizer.TokenizerImpl;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
