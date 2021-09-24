package gitlet;

public class OptLog implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Illegal Commands: log does not allow any other input.";

    public OptLog(String[] args) {
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

        // Read HEAD pointer
        String head = FileController.getHEAD();
        if (head == null) {
            return false;
        }

        // Read current branch
        Branch currBranch = FileController.readBranch(head);
        if (currBranch == null) {
            return false;
        }

        // Read current head commit
        Commit currCommit = FileController.readCommit(currBranch.getCommitId());
        if (currCommit == null) {
            return false;
        }

        // Try to print out the first Message.
        try {
            String commitID = "Commit " + currBranch.getCommitId();
            String commitTime = currCommit.getDate();
            String commitMsg = currCommit.getMsg();
            System.out.println("===");
            System.out.println(commitID);
            System.out.println(commitTime);
            System.out.println(commitMsg);
            System.out.println("");
            if (currCommit.getParentId() != null) {
                logHelper(currCommit.getParentId());
            }
        } finally {
            return true;
        }
    }

    private void logHelper(String parentID) {
        Commit pointer = FileController.readCommit(parentID);
        if (pointer != null) {
            String commitID = "Commit " + parentID;
            String commitTime = pointer.getDate();
            String commitMsg = pointer.getMsg();
            System.out.println("===");
            System.out.println(commitID);
            System.out.println(commitTime);
            System.out.println(commitMsg);
            System.out.println("");
            logHelper(pointer.getParentId());
        }
    }
}
