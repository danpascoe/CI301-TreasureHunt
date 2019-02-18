package application.pascoe.com.ci301.utility;

        import android.content.Context;

        import application.pascoe.com.ci301.security.PasswordHash;
        import application.pascoe.com.ci301.sqlite.SQLDatabase;

public class SQLManager {

    private SQLManager SQLManager;
    private SQLManager(Context context){
        initDB(context);
    }

    private static final String TAG = "SQLManager";
    private SQLDatabase db;

    private void initDB(Context context){
        db = new SQLDatabase(context);
    }

    public SQLManager getInstance(Context context){
        if(SQLManager == null) {
            SQLManager = new SQLManager(context);
        }
        return SQLManager;
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
