package service;



import dao.UserDAO;
import dao.impl.UserDAOImpl;
import model.User;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserService {
    private final UserDAO userDAO = new UserDAOImpl();
    private final Scanner scanner = new Scanner(System.in);

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
                case 1 -> createUser();
                case 2 -> viewAllUsers();
                case 3 -> findUserById();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> {
                    System.out.println("Exiting application...");
                    HibernateUtil.shutdown();
                    System.exit(0);
                }
                default -> System.out.println("Invalid option! Try again.");
            }
        }
    }

    private void createUser() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter age: ");
            Integer age = scanner.nextInt();
            scanner.nextLine();

            User user = new User(name, email, age);
            userDAO.save(user);
            System.out.println("User created successfully! ID: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    private void viewAllUsers() {
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        users.forEach(System.out::println);
    }

    private void findUserById() {
        try {
            System.out.print("Enter user ID: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            Optional<User> user = userDAO.findById(id);
            user.ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println("User not found with ID: " + id)
            );
        } catch (Exception e) {
            System.err.println("Invalid ID format!");
        }
    }

    private void updateUser() {
        try {
            System.out.print("Enter user ID to update: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            Optional<User> optionalUser = userDAO.findById(id);
            if (optionalUser.isEmpty()) {
                System.out.println("User not found!");
                return;
            }

            User user = optionalUser.get();
            System.out.print("Enter new name (current: " + user.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) user.setName(name);

            System.out.print("Enter new email (current: " + user.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) user.setEmail(email);

            System.out.print("Enter new age (current: " + user.getAge() + "): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.isEmpty()) user.setAge(Integer.parseInt(ageInput));

            if (userDAO.update(user)) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("Failed to update user.");
            }
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print("Enter user ID to delete: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            if (userDAO.deleteById(id)) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found or deletion failed.");
            }
        } catch (Exception e) {
            System.err.println("Invalid ID format!");
        }
    }
}