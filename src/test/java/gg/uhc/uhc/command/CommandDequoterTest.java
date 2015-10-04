package gg.uhc.uhc.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class CommandDequoterTest {

    @Test
    public void test() {
        CommandDequoter dequoter = new CommandDequoter();

        String[] test = "-s \"Test String\" -d yes \"\\\"\" pineapple\\ sunday".split(" ");

        String[] deq = dequoter.dequote(test);

        assertThat(deq).containsExactly("-s", "Test String", "-d", "yes", "\"", "pineapple sunday");
    }
}
