package task_manager.api;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;

import task_manager.AppModule;
import task_manager.api.command.Command;
import task_manager.api.use_case.PropertyDescriptorUseCase;
import task_manager.db.property.PropertyDescriptorCollection;
import task_manager.db.property.PropertyManager;
import task_manager.db.task.Task;


public class Executor {
    public void execute(Command command) {
        command.execute(context);
    }

    public boolean shouldExit() {
        return false;
    }

    public Context getContext() {
        return context;
    }

    public static Executor getExecutor() throws IOException {
        Injector injector = Guice.createInjector(new AppModule());

        PropertyDescriptorUseCase propertyDescriptorUseCase = injector.getInstance(PropertyDescriptorUseCase.class);
        PropertyDescriptorCollection propertyDescriptors = propertyDescriptorUseCase.getPropertyDescriptors();

        Task.setPropertyManager(new PropertyManager(propertyDescriptors));

        return injector.getInstance(Executor.class);
    }

    @Inject
    private Context context;

}
