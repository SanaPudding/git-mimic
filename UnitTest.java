package gitlet;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/* The suite of all JUnit tests for the gitlet package.
   @author
 */
public class UnitTest {

    private static final ArrayList<String> CMD_NAME = new ArrayList<String>(
            Arrays.asList("init", "add", "commit", "rm", "log", "global-log", "find",
                    "status", "checkout", "branch", "rm-branch", "reset", "merge")
    );

    @Test
    public void placeholderTest() {
        String[] step1 = {"init"};
        String[] step2 = {"add", "file1"};
        String[] step21 = {"add", "file2"};
        String[] step22 = {"add", "file3"};
        String[] step23 = {"add", "file4"};

        String[] step3 = {"commit", "file1 add master"};

        String[] step4 = {"branch", "other"};

        String[] step41 = {"add", "file3"};
        String[] step42 = {"commit", "file3 add master"};

        String[] step5 = {"checkout", "other"};
        String[] step6 = {"add", "file2"};
        String[] step7 = {"commit", "file2 add other"};
        String[] step8 = {"checkout", "master"};

        String[] step9 = {"status"};

        Main.main(step1);
        Main.main(step2);
        Main.main(step3);

        Main.main(step4);
        Main.main(step41);
        Main.main(step42);

        Main.main(step5);
        Main.main(step6);
        Main.main(step7);
        Main.main(step8);

        Main.main(step9);
    }

}


