package gitlet;

import java.io.File;
import java.util.List;

public class OptGlobalLog implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Illegal Commands: log does not allow any other input.";

    public OptGlobalLog(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length >= 2) {
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

        for (int i = 0; i < commitList.size(); i++) {
            String fileid = commitList.get(i);
            glogHelper(fileid);
        }
        return true;
    }

    void glogHelper(String fileid) {
        Commit logfile = FileController.readCommit(fileid);
        String commitId = "Commit " + fileid;
        String commitDate = logfile.getDate();
        String commitMsg = logfile.getMsg();
        System.out.println("===");
        System.out.println(commitId);
        System.out.println(commitDate);
        System.out.println(commitMsg);
        System.out.println("");
    }
}
