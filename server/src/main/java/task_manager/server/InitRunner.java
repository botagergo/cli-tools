package common.server;

import jakarta.inject.Inject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import common.init.Initializer;

@Component
public class InitRunner implements CommandLineRunner {

    @Inject
    public InitRunner(Initializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public void run(String... args) throws Exception {
        if (initializer.needsInitialization()) {
            initializer.initialize();
        }
    }

    private final Initializer initializer;

}
