package gg.uhc.uhc.modules.timer;

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
        assertThat(TimeUtil.secondsToString(-0)).isEqualTo("0s");
        assertThat(TimeUtil.secondsToString(0)).isEqualTo("0s");
        assertThat(TimeUtil.secondsToString(10)).isEqualTo("10s");
        assertThat(TimeUtil.secondsToString(30)).isEqualTo("30s");
        assertThat(TimeUtil.secondsToString(59)).isEqualTo("59s");
        assertThat(TimeUtil.secondsToString(60)).isEqualTo("1m");
        assertThat(TimeUtil.secondsToString(186)).isEqualTo("3m 6s");
        assertThat(TimeUtil.secondsToString(360)).isEqualTo("6m");
        assertThat(TimeUtil.secondsToString(3599)).isEqualTo("59m 59s");
        assertThat(TimeUtil.secondsToString(3600)).isEqualTo("1h");
        assertThat(TimeUtil.secondsToString(3601)).isEqualTo("1h 1s");
        assertThat(TimeUtil.secondsToString(3660)).isEqualTo("1h 1m");
        assertThat(TimeUtil.secondsToString(3661)).isEqualTo("1h 1m 1s");
        assertThat(TimeUtil.secondsToString(3661)).isEqualTo("1h 1m 1s");
        assertThat(TimeUtil.secondsToString(86400)).isEqualTo("1d");
        assertThat(TimeUtil.secondsToString(90061)).isEqualTo("1d 1h 1m 1s");
    }
}