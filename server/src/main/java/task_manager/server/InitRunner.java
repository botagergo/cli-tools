package task_manager.server;

import jakarta.inject.Inject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import task_manager.init.Initializer;

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
