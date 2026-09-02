package gitlet;

import java.util.List;

public class OptAdd implements Operation {

    private String[] args;
    private final String EXIST_ERR = "File does not exist.";
    private final String ARGS_ERR = "Incorrect operands.";

    public OptAdd(String[] args) {
        this.args = args;
    }

    public boolean isValidArgs() {
        // Check if args contains filename
        if (args.length == 1 | args.length > 2) {
            System.out.println(ARGS_ERR);
            return false;
        }

        // Check if the given file exists at local
        List<String> workingDir = Utils.plainFilenamesIn(FileController.getCurrentPath());
        if (workingDir.size() == 0) {
            System.out.println(EXIST_ERR);
            return false;
        } else if (!workingDir.contains(args[1])) {
            System.out.println(EXIST_ERR);
            return false;
        }

        return true;
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
        // Generate given file's Id according to its contents
        String addedFileId = FileController.createFileId(localPath);

        // Read HEAD pointer
        String head = FileController.getHEAD();
        if (head == null | addedFileId == null) {
            return false;
        }

        // Read current branch
        Branch currBranch = FileController.readBranch(head);
        if (currBranch == null) {
            return false;
        }

        // Read head commit and staging area from local
        Commit currCommit = FileController.readCommit(currBranch.getCommitId());
        Stage stage = FileController.readStage();
        if (currCommit == null | stage == null) {
            return false;
        }

        // Check if the added file is staged for removal before
        if (stage.isRemovemedIn(filename)) {
            stage.rmRemove(filename);
            boolean isStageSaved = FileController.storeStage(stage);
        }

        // First compare with current commit's file, then compare with staging area's file
        if (!currCommit.isTracked(filename)) {
            if (!stage.isAddedIn(filename)) {

                // Add file to stage
                stage.setAdd(filename, addedFileId);

                // Store added file to .gitlet/object
                boolean isFileSaved = FileController.storeFiles(localPath, addedFileId);

                // Store the updated staging area
                boolean isStageSaved = FileController.storeStage(stage);

                return isFileSaved && isStageSaved;

            } else {
                System.out.println("already added");
                return true;
            }
        } else {
            if (!currCommit.getFileId(filename).equals(addedFileId)) {
                if (!stage.isAddedIn(filename)) {

                    // Add file to stage
                    stage.setAdd(filename, addedFileId);

                    // Store added file to .gitlet/object
                    boolean isFileSaved = FileController.storeFiles(localPath, addedFileId);

                    // Store the updated staging area
                    boolean isStageSaved = FileController.storeStage(stage);

                    return isFileSaved && isStageSaved;
                } else {
                    System.out.println("already added");
                    return true;
                }
            } else {
                return true;
            }
        }

    }

}
