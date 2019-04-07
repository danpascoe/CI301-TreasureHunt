// https://junit.org/junit4/javadoc/4.12/org/junit/Assert.html
// http://robolectric.org/
// WHY HAVE I CHOSEN THE TESTS

package application.pascoe.com.ci301;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import androidx.test.core.app.ApplicationProvider;
import application.pascoe.com.ci301.security.BCrypt;
import application.pascoe.com.ci301.sqlite.GameplaySQLDatabase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
public class UnitTesting {

    GameplaySQLDatabase gameplaySQL;

    @Before
    public void Setup(){
        gameplaySQL = new GameplaySQLDatabase(ApplicationProvider.getApplicationContext());
    }


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

    @Test // TEST TO CHECK THE SQL RETURNS POSITIONS
    public void test_GetPositions(){
        double[] Lat = gameplaySQL.getLat("default");
        double[] Lng = gameplaySQL.getLng("default");
        LatLng[] positions = new LatLng[Lat.length];

        if(Lat.length != 0){
            for (int i = 0; i < Lat.length ; i++) {
                positions[i] = new LatLng(Lat[i], Lng[i]);
            }
        }

        assertNotEquals(positions.length, 0);
    }
    
    @Test
    public void test_GetClues(){ // TEST TO CHECK THE SQL RETURNS CLUES
        String[] clues = gameplaySQL.getClues(1);
        assertNotEquals(clues.length, 0);
    }


    @Test // TEST TO CHECK DISTANCE BETWEEN TWO POINTS WILL RETURN TRUE
    public void test_CheckDistanceBetweenLatLngAndCluePos(){
        LatLng UserPosition = new LatLng(50.8719, 0.5745);
        LatLng CluePosition = new LatLng(50.8719, 0.574404);

        SphericalUtil.computeDistanceBetween(UserPosition, CluePosition);

        assertTrue(SphericalUtil.computeDistanceBetween(UserPosition, CluePosition) < 10.0f);
    }
}
