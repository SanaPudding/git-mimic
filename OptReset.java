package gitlet;

import java.util.ArrayList;
import java.util.Set;

public class OptReset implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Incorrect operands.";
    private final String COMMIT_EXIST_ERR = "No commit with that id exists.";

    public OptReset(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length != 2) {
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
        /** Check if given commitId exists */
        String givenCommitId = FileController.findCommit(args[1]);
        if (givenCommitId == null) {
            System.out.println(COMMIT_EXIST_ERR);
            return false;
        }
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
        // Read given commit
        Commit givenCommit = FileController.readCommit(givenCommitId);
        if (givenCommit == null) {
            return false;
        }

        /** Check for untracked file overwrite */
        if (!Commit.noOverwriteErr(currCommit, givenCommit)) {
            return false;
        }
        // Read all tracked file's name-Id pairs from current && given commit
        Set<String> currFiles = currCommit.getAllFileIds().keySet();
        Set<String> givenFiles = givenCommit.getAllFileIds().keySet();

        // Untracked file list
        ArrayList<String> excludeList = new ArrayList<>();

        // Filter out all files in curr not in given branch, and files with same contents
        for (String filename : currFiles) {
            if (!givenFiles.contains(filename)) {
                Utils.restrictedDelete(FileController.getCurrentPath() + "/" + filename);
            } else {
                if (currCommit.getFileId(filename).equals(givenCommit.getFileId(filename))) {
                    excludeList.add(filename);
                }
            }
        }
        // Exclude file with the same content as current branch's one
        for (String filename : excludeList) {
            givenFiles.remove(filename);
        }
        // Overwrite files;
        if (!givenFiles.isEmpty()) {
            for (String filename : givenFiles) {
                String path = FileController.getCurrentPath() + "/" + filename;
                FileController.overwriteFiles(path, givenCommit.getFileId(filename));
            }
        }

        // Move current branch's head pointer
        currBranch.setCommitId(givenCommitId);
        if (!FileController.storeObject(currBranch, "branch", head)) {
            return false;
        }

        // Update staging area
        stage.clear();
        return FileController.storeStage(stage);
    }

}
