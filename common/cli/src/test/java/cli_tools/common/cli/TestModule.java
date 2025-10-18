package cli_tools.common.cli;

import cli_tools.common.cli.tokenizer.Tokenizer;
import cli_tools.common.cli.tokenizer.TokenizerImpl;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
