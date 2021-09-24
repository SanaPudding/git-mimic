package gitlet;

import java.util.Map;

public class OptCommit implements Operation {

    private String[] args;
    private final String EMPTY_STAGE_ERR = "No changes added to the commit.";
    private final String NO_MSG_ERR = "Please enter a commit message.";
    private final String ARGS_ERR = "Incorrect operands.";

    public OptCommit(String[] args) {
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
        } else if (args[1].equals("")) {
            // FIXME
            System.out.println(NO_MSG_ERR);
            return false;
        }

        return true;
    }

    @Override
    public boolean execute() {
        if (!isValidArgs()) {
            return false;
        }
        // Read staging area
        Stage stage = FileController.readStage();
        if (stage == null) {
            return false;
        }
        // Check if staging area is empty
        if (stage.isEmpty()) {
            System.out.println(EMPTY_STAGE_ERR);
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
        /**
         * Generate new commit, and its Id, and assign its parentId
         * */
        Commit newCommit = new Commit(args[1], currBranch.getCommitId());
        // Generate new commit's Id
        String newCommitId = FileController.createObjectId(newCommit);
        // Update parent's commit's children pointers
        currCommit.addChild(newCommitId);
        // Copy all file pointers from CURR commit to NEW
        newCommit.addAllFileId(currCommit.getAllFileIds());
        // Get all stage info
        Map<String, String> addMaps = stage.getAllAdds();
        Map<String, String> removeMaps = stage.getAllRemoves();
        // Track added files
        if (stage.getAddCount() != 0) {
            for (String key : addMaps.keySet()) {
                // Check if the file is tracked before
                if (newCommit.isTracked(key)) {
                    newCommit.replaceFileId(key, addMaps.get(key));
                } else {
                    newCommit.addFileId(key, addMaps.get(key));
                }
            }
        }
        // Untrack removed files
        if (stage.getRemoveCount() != 0) {
            for (String key : removeMaps.keySet()) {
                newCommit.removeFileId(key);
            }
        }
        // Clear staging area
        stage.clear();
        // Update current branch
        currBranch.setCommitId(newCommitId);
        // Store currCommit, newCommit, current branch, and stage to local
        boolean save1 = FileController.storeObject(currCommit, "commit", newCommit.getParentId());
        boolean save2 = FileController.storeObject(newCommit, "commit", newCommitId);
        boolean save3 = FileController.storeObject(currBranch, "branch", head);
        boolean save4 = FileController.storeStage(stage);
        return save1 && save2 && save3 && save4;
    }

}
