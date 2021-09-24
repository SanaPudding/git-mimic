package gitlet;

public class OptRemove implements Operation {

    private String[] args;
    private final String NEITHER_STAGED_NOR_TRACKED_ERR = "No reason to remove the file.";
    private final String ARGS_ERR = "Incorrect operands.";

    public OptRemove(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length == 1 | args.length > 2) {
            System.out.println(ARGS_ERR);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean execute() {
        if (!isValidArgs()) {
            return false;
        }

        // Assign file name
        String filename = args[1];

        // Generate added file's local path
        String localPath = FileController.getCurrentPath() + "/" + args[1];

        // Read staging area
        Stage stage = FileController.readStage();
        if (stage == null) {
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

        // Check if the file is tracked
        if (!currCommit.isTracked(args[1]) && !stage.isAddedIn(filename)) {
            System.out.println(NEITHER_STAGED_NOR_TRACKED_ERR);
            return false;
        }

        // Check if the file is tracked by current head commit
        if (currCommit.isTracked(args[1])) {
            // Unstage the file if had been staged before
            if (stage.isAddedIn(filename)) {
                stage.rmAdd(filename);
            }

            // Remove the file from local
            boolean isLocalRemoved = Utils.restrictedDelete(localPath);

            // Stage the file for remove
            if (!stage.isRemovemedIn(filename)) {
                stage.setRemove(filename, currCommit.getFileId(filename));
            }
        } else {
            // Unstage the file if had been staged before
            if (stage.isAddedIn(filename)) {
                stage.rmAdd(filename);
            }
        }

        // Store stage to local

        return FileController.storeStage(stage);
    }

}
