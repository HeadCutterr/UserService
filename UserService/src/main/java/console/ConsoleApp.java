package console;

import model.User;
import userservice.service.UserService;
import userservice.service.UserServiceImpl;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp() {
        this.userService = new UserServiceImpl();
    }

    public ConsoleApp(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== User Management System ===");
            System.out.println("1. Create User");
            System.out.println("2. View All Users");
            System.out.println("3. Find User by ID");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> handleCreateUser();
                case 2 -> handleViewAllUsers();
                case 3 -> handleFindUserById();
                case 4 -> handleUpdateUser();
                case 5 -> handleDeleteUser();
                case 6 -> {
                    System.out.println("Exiting application...");
                    HibernateUtil.shutdown();
                    System.exit(0);
                }
                default -> System.out.println("Invalid option! Try again.");
            }
        }
    }

    private void handleCreateUser() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter age: ");
            String ageInput = scanner.nextLine().trim();
            int age = Integer.parseInt(ageInput);

            User user = userService.createUser(name, email, age);
            System.out.println("User created successfully! ID: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.out.println("Operation cancelled. Return to menu...");
        }
    }

    private void handleViewAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        users.forEach(System.out::println);
    }

    private void handleFindUserById() {
        try {
            System.out.print("Enter user ID: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                System.out.println(user.get());
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleUpdateUser() {
        try {
            System.out.print("Enter user ID to update: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isEmpty()) {
                System.out.println("User not found with ID: " + id);
                return;
            }

            User user = optionalUser.get();

            System.out.print("Enter new name (current: " + user.getName() + "): ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter new email (current: " + user.getEmail() + "): ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter new age (current: " + user.getAge() + "): ");
            String ageInput = scanner.nextLine().trim();
            Integer age = ageInput.isEmpty() ? null : Integer.parseInt(ageInput);

            boolean updated = userService.updateUser(id,
                    name.isEmpty() ? null : name,
                    email.isEmpty() ? null : email,
                    ageInput.isEmpty() ? null : age
            );

            System.out.println(updated ? "User updated successfully!" : "Failed to update user");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleDeleteUser() {
        try {
            System.out.print("Enter user ID to delete: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            boolean deleted = userService.deleteUser(id);
            System.out.println(deleted ? "User deleted successfully!" : "User not found or deletion failed.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ConsoleApp().start();
    }
}