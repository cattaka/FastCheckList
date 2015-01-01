package net.cattaka.android.fastchecklist.exception;

import junit.framework.TestCase;

public class DbExceptionTest extends TestCase {
    public void testConstructor_1() {
        DbException sup = new DbException();
        assertNotNull(sup);
        assertNull(sup.getMessage());
        assertNull(sup.getCause());
    }
    public void testConstructor_2() {
        Throwable t = new Throwable();
        DbException sup = new DbException("detailMessage", t);
        assertNotNull(sup);
        assertEquals("detailMessage", sup.getMessage());
        assertEquals(t, sup.getCause());
    }
    public void testConstructor_3() {
        Throwable t = new Throwable();
        DbException sup = new DbException("detailMessage");
        assertNotNull(sup);
        assertEquals("detailMessage", sup.getMessage());
        assertNull(sup.getCause());
    }
    public void testConstructor_4() {
        Throwable t = new Throwable();
        DbException sup = new DbException(t);
        assertNotNull(sup);
        assertEquals(t.getClass().getCanonicalName(), sup.getMessage());
        assertEquals(t, sup.getCause());
    }
}