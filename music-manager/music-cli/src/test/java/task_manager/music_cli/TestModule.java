package task_manager.music_cli;

import com.google.inject.AbstractModule;
import task_manager.cli_lib.tokenizer.Tokenizer;
import task_manager.cli_lib.tokenizer.TokenizerImpl;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
