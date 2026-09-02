package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @author
*/
public class Main {

    // A list of valid command names
    private static final ArrayList<String> CMD_NAME = new ArrayList<String>(
            Arrays.asList("init", "add", "commit", "rm", "log", "global-log", "find",
                    "status", "checkout", "branch", "rm-branch", "reset", "merge")
    );

    // Error messages
    private static final String NO_ARGS_ERR = "Please enter a command.";
    private static final String NAME_INVALID_ERR = "No command with that name exists.";
    private static final String NO_INIT_ERR = "Not in an initialized gitlet directory.";

    /* Usage: java gitlet.Main ARGS, where ARGS contains
       <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println(NO_ARGS_ERR);
            return;
        }
        if (!CMD_NAME.contains(args[0])) {
            System.out.println(NAME_INVALID_ERR);
            return;
        }
        if (!isInit(args)) {
            System.out.println(NO_INIT_ERR);
            return;
        }

        Map<String, Object> cmdMap = new HashMap<>();
        cmdMap.put("init", new OptInit(args));
        cmdMap.put("add", new OptAdd(args));
        cmdMap.put("commit", new OptCommit(args));
        cmdMap.put("rm", new OptRemove(args));
        cmdMap.put("log", new OptLog(args));
        cmdMap.put("global-log", new OptGlobalLog(args));
        cmdMap.put("find", new OptFind(args));
        cmdMap.put("status", new OptStatus(args));
        cmdMap.put("checkout", new OptCheckout(args));
        cmdMap.put("branch", new OptBranch(args));
        cmdMap.put("rm-branch", new OptRemoveBranch(args));
        cmdMap.put("reset", new OptReset(args));
        cmdMap.put("merge", new OptMerge(args));

        Operation cmd = (Operation) cmdMap.get(args[0]);
        cmd.execute();
    }

    private static boolean isInit(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("init")) {
                return true;
            }
        }
        String currPath = System.getProperty("user.dir") + "/.gitlet";
        File gitletRepo = new File(currPath);
        return gitletRepo.exists();
    }

}
