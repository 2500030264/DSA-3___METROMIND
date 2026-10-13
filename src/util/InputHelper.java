package util;

import java.util.Scanner;

/**
 * Robust terminal input helper and UI formatting utility for MetroMind.
 * Prevents input crashes, handles empty lines, range validation, and formatted tables.
 */
public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) return "";
            try {
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) return input;
                System.out.print("Input cannot be empty. Please re-enter: ");
            } catch (Exception e) {
                return "";
            }
        }
    }

    public static String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt + (defaultValue != null && !defaultValue.isEmpty() ? " [" + defaultValue + "]: " : ": "));
        if (!scanner.hasNextLine()) return defaultValue;
        try {
            String input = scanner.nextLine().trim();
            return input.isEmpty() ? defaultValue : input;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) return 0;
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer format. Please enter a valid number.");
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.printf("Please enter a value between %d and %d.%n", min, max);
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) return 0.0;
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal format. Please enter a valid number.");
            } catch (Exception e) {
                return 0.0;
            }
        }
    }

    public static double readDoubleMin(String prompt, double min) {
        while (true) {
            double val = readDouble(prompt);
            if (val >= min) {
                return val;
            }
            System.out.printf("Value must be at least %.2f.%n", min);
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            if (!scanner.hasNextLine()) return false;
            try {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("y") || input.equals("yes")) return true;
                if (input.equals("n") || input.equals("no")) return false;
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static void pause() {
        System.out.print("\nPress [Enter] to continue...");
        if (scanner.hasNextLine()) {
            try {
                scanner.nextLine();
            } catch (Exception ignored) {}
        }
    }

    public static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(64));
        System.out.printf(" %s%n", title.toUpperCase());
        System.out.println("=".repeat(64));
    }

    public static void printSubHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }

    public static void printDivider() {
        System.out.println("-".repeat(64));
    }
}
