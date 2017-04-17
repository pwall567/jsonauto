/*
 * @(#) TestDates.java
 */

package net.pwall.json.auto;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 *
 */
public class TestDates {

    @Test
    public void test() {
        Date date1 = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.clear();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.clear();
        cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
        cal2.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
        cal2.set(Calendar.DAY_OF_MONTH, cal1.get(Calendar.DAY_OF_MONTH));
        cal2.set(Calendar.HOUR_OF_DAY, cal1.get(Calendar.HOUR_OF_DAY));
        cal2.set(Calendar.MINUTE, cal1.get(Calendar.MINUTE));
        cal2.set(Calendar.SECOND, cal1.get(Calendar.SECOND));
        cal2.set(Calendar.MILLISECOND, cal1.get(Calendar.MILLISECOND));
        cal2.set(Calendar.ZONE_OFFSET, cal1.get(Calendar.ZONE_OFFSET));
        assertEquals(cal1, cal2);
        Date date2 = cal2.getTime();
        assertEquals(date1, date2);
    }

}
