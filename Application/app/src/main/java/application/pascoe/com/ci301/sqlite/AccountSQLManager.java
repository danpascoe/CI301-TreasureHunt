package application.pascoe.com.ci301.sqlite;

import android.content.Context;
import application.pascoe.com.ci301.security.PasswordHash;
import application.pascoe.com.ci301.utility.Status;

public class AccountSQLManager {

    public AccountSQLManager(Context context){
        initDB(context);
    }

    private static final String TAG = "AccountSQLManager";
    private AccountSQLDatabase db;

    private void initDB(Context context){
        db = new AccountSQLDatabase(context);
    }

    public String[] createAccount(String username, String password){
        String hashedPassword = PasswordHash.hashPassword(password);
        String[] returnArr = db.insertUser(username, hashedPassword);
        return returnArr;
    }

    public String[] checkLogin(String username, String password){
        String storedHash;
        if(db.checkUsername(username) == 1){
            String[] returnInfo;
            returnInfo = db.getHashedPassword(username);
            if(returnInfo[0] == "SUCCESS") {
                storedHash = returnInfo[1];
            } else {
                return returnInfo;
            }
        }else {
            String[] returnInfo = {Status.FAILED.toString(), "USER DOES NOT EXIST"};
            return returnInfo;
        }

        if(!PasswordHash.checkHashedPassword(password, storedHash)){
            String[] returnInfo = {Status.FAILED.toString(), "PASSWORD OR USER IS INCORRECT"};
            return returnInfo;
        }

        String[] returnInfo = {Status.SUCCESS.toString(), "SIGNED IN"};
        return returnInfo;
    }
}