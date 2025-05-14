package org.cron;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CronSchedulerUtilTest {
    private static CronSchedulerUtil util;
    @BeforeAll
    static void setup() {
        util = new CronSchedulerUtil();
    }

    @Test
    void testWildcardMatch() {
        assertTrue(util.matches(15, "*", 0, 59));
    }

    @Test
    void testExactMatch() {
        assertTrue(util.matches(15, "15", 0, 59));
        assertFalse(util.matches(10, "15", 0, 59));
    }

    @Test
    void testStepMatch() {
        assertTrue(util.matches(10, "*/5", 0, 59));
        assertFalse(util.matches(7, "*/5", 0, 59));
    }

    @Test
    void testRangeMatch() {
        assertTrue(util.matches(5, "3-6", 0, 59));
        assertFalse(util.matches(7, "3-6", 0, 59));
    }
}
