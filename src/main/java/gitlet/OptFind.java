package gitlet;

import java.io.File;
import java.util.List;

public class OptFind implements Operation {

    private String[] args;
    private final String NO_MSG_ERR = "Please enter a commit message.";
    private final String ARGS_ERR = "Incorrect operands.";

    public OptFind(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length == 1) {
            System.out.println(NO_MSG_ERR);
            return false;
        } else if (args.length > 2) {
            System.out.println(ARGS_ERR);
            return false;
        }
        return true;
    }

    @Override
    public boolean execute() {
        if (!isValidArgs()) {
            return false;
        }

        // Read commit directory.
        File commitDir =  new File(FileController.getCommitPath());
        if (commitDir == null) {
            return false;
        }

        // Read file names in commit directory
        List<String> commitList = Utils.plainFilenamesIn(commitDir);
        if (commitList == null) {
            return false;
        }

        boolean fStatus = false;
        for (int i = 0; i < commitList.size(); i++) {
            String fileid = commitList.get(i);
            Commit searchCommit = FileController.readCommit(fileid);
            if (searchCommit.getMsg().equals(args[1])) {
                System.out.println(fileid);
                fStatus = true;
            }
        }

        if (!fStatus) {
            System.out.println("Found no commit with that message.");
        }

        return true;
    }
}
