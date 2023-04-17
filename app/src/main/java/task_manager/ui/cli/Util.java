package task_manager.ui.cli;

import java.util.Scanner;

public class Util {
    public static boolean yesNo(String prompt) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.print(prompt + " (Y/n)");
        String answer = scanner.nextLine();
        System.out.println();

        if (answer.equals("Y")) {
            return true;
        } else {
            return false;
        }
    }
}
