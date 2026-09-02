package gitlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptCheckout implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Incorrect operands.";
    private final String FILE_EXIST_ERR = "File does not exist in that commit.";
    private final String COMMIT_EXIST_ERR = "No commit with that id exists.";
    private final String BRANCH_EXIST_ERR = "No such branch exists.";
    private final String CURRENT_BRANCH_ERR = "No need to checkout the current branch.";

    public OptCheckout(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length == 1 | args.length > 4) {
            System.out.println(ARGS_ERR);
            return false;
        }

        // Check style for case 1: Checkout file from head commit
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println(ARGS_ERR);
                return false;
            }
        }

        // Check style for case 2: Checkout file from a given commit
        if (args.length == 4) {
            if (args[1].length() > 40) {
                System.out.println(COMMIT_EXIST_ERR);
                return false;
            } else if (!args[2].equals("--")) {
                System.out.println(ARGS_ERR);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean execute() {
        if (!isValidArgs()) {
            return false;
        }
        String head = FileController.getHEAD();
        Branch currBranch = FileController.readBranch(head);
        if (currBranch == null) {
            return false;
        }
        Commit currCommit = FileController.readCommit(currBranch.getCommitId());
        if (currCommit == null) {
            return false;
        }
        Stage stage = FileController.readStage();
        if (stage == null) {
            return false;
        }
        if (args.length == 3) {
            if (!case1Check(currCommit)) {
                return false;
            }
            String localpath = FileController.getCurrentPath() + "/" + args[2];
            String fileId = currCommit.getFileId(args[2]);
            return FileController.overwriteFiles(localpath, fileId);
        }
        if (args.length == 4) {
            if (!case2Check()) {
                return false;
            }
            String givenCommitId = FileController.findCommit(args[1]);
            Commit givenCommit = FileController.readCommit(givenCommitId);
            String localpath = FileController.getCurrentPath() + "/" + args[3];
            String fileId = givenCommit.getFileId(args[3]);
            return FileController.overwriteFiles(localpath, fileId);
        }
        if (args.length == 2) {
            if (!case3Check(head, currCommit)) {
                return false;
            }
            String givenbranchName = args[1];
            Branch givenBranch = FileController.readBranch(givenbranchName);
            if (givenBranch == null) {
                return false;
            }
            Commit givenCommit = FileController.readCommit(givenBranch.getCommitId());
            if (givenCommit == null) {
                return false;
            }
            Map<String, String> currFileIdPairs = currCommit.getAllFileIds();
            Map<String, String> givenFileIdPairs = givenCommit.getAllFileIds();
            Set<String> currTrackedFiles = currFileIdPairs.keySet();
            Set<String> givenTrackedFiles = givenFileIdPairs.keySet();
            ArrayList<String> excludeList = new ArrayList<>();
            for (String filename : currTrackedFiles) {
                if (!givenTrackedFiles.contains(filename)) {
                    Utils.restrictedDelete(FileController.getCurrentPath() + "/" + filename);
                } else {
                    if (currFileIdPairs.get(filename).equals(givenFileIdPairs.get(filename))) {
                        excludeList.add(filename);
                    }
                }
            }
            for (String filename : excludeList) {
                givenTrackedFiles.remove(filename);
            }
            if (!givenTrackedFiles.isEmpty()) {
                for (String filename : givenTrackedFiles) {
                    String path = FileController.getCurrentPath() + "/" + filename;
                    FileController.overwriteFiles(path, givenFileIdPairs.get(filename));
                }
            }
            String headPath = FileController.getCurrentPath() + "/.gitlet/HEAD/HEAD";
            FileController.writeFile(headPath, givenbranchName);
            stage.clear();
            return FileController.storeStage(stage);
        }
        return true;
    }

    // Check for failure cases of Case 1
    private boolean case1Check(Commit currCommit) {

        // Assign file name
        String filename = args[2];

        // Check for file existence from current commit
        if (!isFileExist(filename, currCommit)) {
            return false;
        }

        return true;
    }

    /**
     * Case Checkers
     * */

    // Check for failure cases of Case 2
    private boolean case2Check() {

        // Assign given commit Id
        String givenCommitId = args[1];

        // Assign filename
        String filename = args[3];

        // Check for commit existence
        String foundedCommitId = FileController.findCommit(givenCommitId);
        if (foundedCommitId == null) {
            System.out.println(COMMIT_EXIST_ERR);
            return false;
        }

        // Read the given commit
        Commit foundedCommit = FileController.readCommit(foundedCommitId);
        if (foundedCommit == null) {
            return false;
        }

        // Check for file existence from given commit
        if (!isFileExist(filename, foundedCommit)) {
            return false;
        }

        return true;
    }

    // Check for failure cases of Case 3
    private boolean case3Check(String head, Commit currCommit) {
        // Assign branch name
        String branchName = args[1];

        // Check if given branch is the HEAD branch
        if (branchName.equals(head)) {
            System.out.println(CURRENT_BRANCH_ERR);
            return false;
        }

        // Check for branch existence
        List<String> branches = Branch.getAllName();
        if (!branches.contains(branchName)) {
            System.out.println(BRANCH_EXIST_ERR);
            return false;
        }

        // Read given branch
        Branch givenBranch = FileController.readBranch(branchName);
        if (givenBranch == null) {
            return false;
        }

        // Read given branch's head commit
        Commit givenCommit = FileController.readCommit(givenBranch.getCommitId());
        if (givenCommit == null) {
            return false;
        }

        /** Check for untracked file overwrite */
        return Commit.noOverwriteErr(currCommit, givenCommit);
    }

    // Helper function check for file existence
    private boolean isFileExist(String filename, Commit commit) {
        if (!commit.isTracked(filename)) {
            System.out.println(FILE_EXIST_ERR);
            return false;
        }
        return true;
    }

}
