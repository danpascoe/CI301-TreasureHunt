package application.pascoe.com.ci301.security;

public class PasswordHash {

    public static String hashPassword(String password){
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }

    public static boolean checkHashedPassword(String inputPassword, String hashedPassword){
        Boolean passwordCheck;
        passwordCheck = BCrypt.checkpw(inputPassword, hashedPassword);
        return passwordCheck;
    }
}
