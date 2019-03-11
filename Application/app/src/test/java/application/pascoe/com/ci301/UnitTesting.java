// https://junit.org/junit4/javadoc/4.12/org/junit/Assert.html

package application.pascoe.com.ci301;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import static org.junit.Assert.*;
import org.junit.Test;
import application.pascoe.com.ci301.security.BCrypt;
import application.pascoe.com.ci301.sqlite.AccountSQLManager;

public class UnitTesting {

    @Test // TEST TO CHECK PASSWORD IS ENCRYPTED
    public void test_HashPassword(){
        String StringToHash = "TestHashString_0123";
        String HashedString = BCrypt.hashpw(StringToHash, BCrypt.gensalt());
        assertNotEquals(StringToHash, HashedString);
    }

    @Test // TEST TO CHECK PASSWORD CHECKING IS CORRECT AND WORKING
    public void test_CheckPassword(){
        String PasswordInput = "PasswordTest123";
        String StoredPassword = "$2a$10$Yumv0SAfKJ8dR1UTnvHbTOrR2noCdd4KVa4xisgH2MfPfCE5kR/Yi";

        boolean passwordCheck;
        passwordCheck = BCrypt.checkpw(PasswordInput, StoredPassword);
        assertTrue(passwordCheck);
    }

    @Test // TEST TO CHECK PASSWORD CHECKING WILL PICK UP WRONG PASSWORDS
    public void test_CheckWrongPassword(){
        String PasswordInput = "WrongPassword";
        String StoredPassword = "$2a$10$Yumv0SAfKJ8dR1UTnvHbTOrR2noCdd4KVa4xisgH2MfPfCE5kR/Yi";

        boolean passwordCheck;
        passwordCheck = BCrypt.checkpw(PasswordInput, StoredPassword);
        assertFalse(passwordCheck);
    }

    @Test // TEST TO CHECK DISTANCE BETWEEN TWO POINTS WILL RETURN TRUE
    public void test_CheckDistanceBetweenLatLngAndCluePos(){
        LatLng UserPosition = new LatLng(50.8719, 0.5745);
        LatLng CluePosition = new LatLng(50.8719, 0.574404);
        SphericalUtil.computeDistanceBetween(UserPosition, CluePosition);
        boolean distanceCheck = false;

        if(SphericalUtil.computeDistanceBetween(UserPosition, CluePosition) < 10.0f){
            distanceCheck = true;
        }
        assertTrue(distanceCheck);
    }
}
