package task_manager.ui.cli;

import com.google.inject.AbstractModule;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.UUIDGenerator;
import task_manager.ui.cli.tokenizer.Tokenizer;
import task_manager.ui.cli.tokenizer.TokenizerImpl;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(UUIDGenerator.class).to(RoundRobinUUIDGenerator.class);
    }

}
