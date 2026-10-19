package chatverse;

import java.util.Scanner;

public class ChatVerse {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginApp = new Login();

        System.out.println("======================================");
        System.out.println("     WELCOME TO CHATVERSE USER REG    ");
        System.out.println("======================================");

        System.out.print("Enter First Name: ");
        loginApp.setFirstName(scanner.nextLine());

        System.out.print("Enter Last Name: ");
        loginApp.setLastName(scanner.nextLine());

        // Step 1: Username Validation Loop
        while (true) {
            System.out.print("Enter Username: ");
            loginApp.setUsername(scanner.nextLine());
            if (loginApp.checkUserName()) {
                System.out.println("Username successfully captured.");
                break;
            } else {
                System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            }
        }

        // Step 2: Password Validation Loop
        while (true) {
            System.out.print("Enter Password: ");
            loginApp.setPassword(scanner.nextLine());
            if (loginApp.checkPasswordComplexity()) {
                System.out.println("Password successfully captured.");
                break;
            } else {
                System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        }

        // Step 3: Cell Phone Validation Loop
        while (true) {
            System.out.print("Enter SA Cell Phone Number (+27...): ");
            loginApp.setCellPhoneNumber(scanner.nextLine());
            if (loginApp.checkCellPhoneNumber()) {
                System.out.println("Cell phone number successfully added.");
                break;
            } else {
                System.out.println("Cell phone number incorrectly formatted or does not contain international code; please correct the number and try again.");
            }
        }

        System.out.println("\nRegistration Successful!\n");

        // Step 4: Login Process
        System.out.println("======================================");
        System.out.println("        WELCOME TO CHATVERSE LOGIN    ");
        System.out.println("======================================");

        while (true) {
            System.out.print("Enter Username: ");
            String loginUsername = scanner.nextLine();

            System.out.print("Enter Password: ");
            String loginPassword = scanner.nextLine();

            boolean isSuccess = loginApp.loginUser(loginUsername, loginPassword);
            System.out.println(loginApp.returnLoginStatus(isSuccess));

            if (isSuccess) {
                break;
            }
        }

        scanner.close();
    }
}