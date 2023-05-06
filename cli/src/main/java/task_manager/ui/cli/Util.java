package task_manager.ui.cli;

import java.util.Scanner;

public class Util {
    public static boolean yesNo(String prompt) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(prompt + " (y/n)");
        String answer = scanner.nextLine();
        System.out.println();

        return answer.equalsIgnoreCase("y");
    }
}
