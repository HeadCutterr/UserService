import service.UserService;
import util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        try {
            UserService userService = new UserService();
            userService.start();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}