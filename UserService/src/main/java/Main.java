import console.ConsoleApp;
import userservice.service.UserService;
import userservice.service.UserServiceImpl;
import util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        ConsoleApp consoleApp = new ConsoleApp(userService);
        consoleApp.start();
    }
}