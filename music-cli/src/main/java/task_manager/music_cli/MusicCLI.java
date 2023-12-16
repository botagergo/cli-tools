package task_manager.music_cli;

import java.io.IOException;
import java.util.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.cli_lib.tokenizer.TokenList;
import task_manager.music_cli.command_line.CommandLine;
import task_manager.music_cli.command_line.Executor;

@Log4j2
public class MusicCLI {

    public static void main(String @NonNull [] args) {
        Injector injector = Guice.createInjector(new AppModule());

        try {
            if (args.length >= 1) {
                Executor executor = injector.getInstance(Executor.class);
                executor.execute(getTokenList(args));
            } else {
                CommandLine commandLine = injector.getInstance(CommandLine.class);
                commandLine.run();
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
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
