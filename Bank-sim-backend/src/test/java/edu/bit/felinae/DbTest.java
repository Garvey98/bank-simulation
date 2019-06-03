package edu.bit.felinae;

import org.junit.Test;

import static org.junit.Assert.*;

public class DbTest {
    private Database db = Database.getInstance();
    @Test public void testAuth(){
        db.cleanDB();
        db.register("124", "111");
        assertTrue(db.checkCreditial("124", "111"));
        assertFalse(db.checkCreditial("124", "122"));
        assertFalse(db.register("124", "0"));
        assertEquals(0, db.checkBalance("124"), 0.01);
        assertTrue(db.deposit("124", 10));
        assertEquals(10, db.checkBalance("124"), 0.01);
        assertTrue(db.withdrawal("124", 5));
        assertEquals(5, db.checkBalance("124"), 0.01);
        assertFalse(db.withdrawal("124", 10));
        assertEquals(5, db.checkBalance("124"), 0.01);
    }
}
