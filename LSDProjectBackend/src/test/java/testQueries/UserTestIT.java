/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testQueries;

import DBLayer.UserQueries;
import entities.User;
import java.sql.SQLException;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Lasse
 */
public class UserTestIT {

    private static UserQueries uq;
    
    @BeforeClass
    public static void setUp(){
        uq = new UserQueries();
    }
    
    @AfterClass
    public static void  cleanUp() throws SQLException{
        uq.removeUser("einstein");
        uq.removeUser("poobread");
    }
    
    @Test
    public void insertUser() throws SQLException {
        User lookup;
        int before, after;

        before = uq.countUsers();

        User u1 = new User(-1, "user", "poobread", "abcdef");
        User u2 = new User(-1, "user", "einstein", "dasdas");

        uq.insertUser(u1);
        uq.insertUser(u2);

        after = uq.countUsers();

        assertEquals(before + 2, after);

        lookup = uq.getUserByName("einstein");
        assertEquals("einstein", lookup.username);
        assertEquals("dasdas", lookup.userpassword);
        assertNotEquals(-1, lookup.userid);
    }
}
