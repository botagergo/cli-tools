package common.music_cli;

import com.google.inject.AbstractModule;
import common.cli.tokenizer.Tokenizer;
import common.cli.tokenizer.TokenizerImpl;
import common.util.RoundRobinUUIDGenerator;
import common.util.UUIDGenerator;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
