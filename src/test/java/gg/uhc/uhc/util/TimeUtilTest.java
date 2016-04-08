/*
 * Project: UHC
 * Class: gg.uhc.uhc.util.TimeUtilTest
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.util;

import gg.uhc.uhc.util.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(PowerMockRunner.class)
public class TimeUtilTest {

    @Test
    public void testGetUnits() {
        // test regular
        assertThat(TimeUtil.getUnits("12d30m")).containsOnly(entry(TimeUnit.DAYS, 12L), entry(TimeUnit.MINUTES, 30L));
        // test spacing
        assertThat(TimeUtil.getUnits("12d 30m")).containsOnly(entry(TimeUnit.DAYS, 12L), entry(TimeUnit.MINUTES, 30L));
        assertThat(TimeUtil.getUnits("12 d 30 m")).containsOnly(entry(TimeUnit.DAYS, 12L), entry(TimeUnit.MINUTES, 30L));
        assertThat(TimeUtil.getUnits("12d, 30 m")).containsOnly(entry(TimeUnit.DAYS, 12L), entry(TimeUnit.MINUTES, 30L));
        // test longer names
        assertThat(TimeUtil.getUnits("12days 30minutes")).containsOnly(entry(TimeUnit.DAYS, 12L), entry(TimeUnit.MINUTES, 30L));
        // test order
        assertThat(TimeUtil.getUnits("30m12d")).containsOnly(entry(TimeUnit.DAYS, 12L), entry(TimeUnit.MINUTES, 30L));
        // test negative
        assertThat(TimeUtil.getUnits("-12m 14seconds")).containsOnly(entry(TimeUnit.MINUTES, -12L), entry(TimeUnit.SECONDS, 14L));

        // test no unit
        assertThat(TimeUtil.getUnits("15m30")).containsOnly(entry(TimeUnit.MINUTES, 15L));
        assertThat(TimeUtil.getUnits("15,30s")).containsOnly(entry(TimeUnit.SECONDS, 30L));

        // nothing
        assertThat(TimeUtil.getUnits("").isEmpty());
        assertThat(TimeUtil.getUnits("askhdiushfiuh").isEmpty());

        // invalid unit
        assertThat(TimeUtil.getUnits("12w")).isEmpty();
    }

    @Test
    public void testGetSeconds() {
        assertThat(TimeUtil.getSeconds("12d30m")).isEqualTo(1036800 + 1800);
        assertThat(TimeUtil.getSeconds("-12d30m")).isEqualTo(-1036800 + 1800);
        assertThat(TimeUtil.getSeconds("15m30s")).isEqualTo(900 + 30);
        assertThat(TimeUtil.getSeconds("kjsodjioj")).isEqualTo(0);
    }

    @Test
    public void testSecondsToString() {
        assertThat(TimeUtil.secondsToString(-10)).isEqualTo("-10s");
        assertThat(TimeUtil.secondsToString(-0)).isEqualTo("00s");
        assertThat(TimeUtil.secondsToString(0)).isEqualTo("00s");
        assertThat(TimeUtil.secondsToString(10)).isEqualTo("10s");
        assertThat(TimeUtil.secondsToString(30)).isEqualTo("30s");
        assertThat(TimeUtil.secondsToString(59)).isEqualTo("59s");
        assertThat(TimeUtil.secondsToString(60)).isEqualTo("01m");
        assertThat(TimeUtil.secondsToString(186)).isEqualTo("03m 06s");
        assertThat(TimeUtil.secondsToString(360)).isEqualTo("06m");
        assertThat(TimeUtil.secondsToString(3599)).isEqualTo("59m 59s");
        assertThat(TimeUtil.secondsToString(3600)).isEqualTo("01h");
        assertThat(TimeUtil.secondsToString(3601)).isEqualTo("01h 01s");
        assertThat(TimeUtil.secondsToString(3660)).isEqualTo("01h 01m");
        assertThat(TimeUtil.secondsToString(3661)).isEqualTo("01h 01m 01s");
        assertThat(TimeUtil.secondsToString(3661)).isEqualTo("01h 01m 01s");
        assertThat(TimeUtil.secondsToString(86400)).isEqualTo("01d");
        assertThat(TimeUtil.secondsToString(90061)).isEqualTo("01d 01h 01m 01s");
    }
}