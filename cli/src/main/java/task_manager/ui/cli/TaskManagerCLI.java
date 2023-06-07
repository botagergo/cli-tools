package task_manager.ui.cli;

import java.io.IOException;
import java.util.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.ui.cli.command_line.CommandLine;
import task_manager.ui.cli.command_line.Executor;
import task_manager.ui.cli.tokenizer.TokenList;

public class TaskManagerCLI {

    public static void main(String @NonNull [] args) throws IOException {
        Injector injector = Guice.createInjector(new AppModule());

        if (args.length >= 1) {
            Executor executor = injector.getInstance(Executor.class);
            executor.execute(getTokenList(args));
        } else {
            CommandLine commandLine = injector.getInstance(CommandLine.class);
            commandLine.run();
        }
    }

    private static @NonNull TokenList getTokenList(String[] args) {
        List<String> argList = new ArrayList<>();
        HashSet<Pair<Integer, Integer>> escapedIndices = new HashSet<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < arg.length();) {
                if (arg.charAt(j) == '\\' && j < arg.length()-1) {
                    escapedIndices.add(Pair.of(i, j));
                    sb.append(arg.charAt(j+1));
                    j += 2;
                } else {
                    sb.append(arg.charAt(j));
                    j += 1;
                }
            }
            argList.add(sb.toString());
        }

        return new TokenList(argList, escapedIndices);
    }

}
