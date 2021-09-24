package gitlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OptMerge implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Incorrect operands.";
    private final String STAGE_ERR = "You have uncommitted changes.";
    private final String BRANCH_EXIST_ERR = "A branch with that name does not exist.";
    private final String SELF_MERGE_ERR = "Cannot merge a branch with itself.";

    private final String ANCESTOR_BRANCH_MSG = "Given branch is an ancestor of the current branch.";
    private final String FAST_FORWARD_MSG = "Current branch fast-forwarded.";
    private final String CONFLICT_MSG = "Encountered a merge conflict.";

    private static boolean conflictExist = false;

    public OptMerge(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length != 2) {
            System.out.println(ARGS_ERR);
            return false;
        }

        /** Check if staging area is empty */
        // Read staging area
        Stage stage = FileController.readStage();
        if (stage == null) {
            return false;
        }

        // Check if staging area is empty
        if (!stage.isEmpty()) {
            System.out.println(STAGE_ERR);
            return false;
        }

        /** Check if the given branch exist */
        List<String> branches = Branch.getAllName();
        if (!branches.contains(args[1])) {
            System.out.println(BRANCH_EXIST_ERR);
            return false;
        }

        // Read HEAD pointer
        String head = FileController.getHEAD();
        if (head == null) {
            return false;
        }

        /** Check if it's trying to merge with itself */
        if (head.equals(args[1])) {
            System.out.println(SELF_MERGE_ERR);
            return false;
        }

        return true;
    }

    @Override
    public boolean execute() {
        if (!isValidArgs()) {
            return false;
        }
        // Assign given branch name
        String givenBranchName = args[1];
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
        // Read current/given branch
        Branch currBranch = FileController.readBranch(head);
        if (currBranch == null) {
            return false;
        }
        Branch givenBranch = FileController.readBranch(givenBranchName);
        if (givenBranch == null) {
            return false;
        }
        // Read current/given head commit
        Commit currCommit = FileController.readCommit(currBranch.getCommitId());
        if (currCommit == null) {
            return false;
        }
        Commit givenCommit = FileController.readCommit(givenBranch.getCommitId());
        if (givenCommit == null) {
            return false;
        }
        /** Check for untracked file overwrite */
        if (!Commit.noOverwriteErr(currCommit, givenCommit)) {
            return false;
        }
        // Find split point
        Commit splitCommit = Commit.findSplitPoint(currCommit, givenCommit);
        // Ancestor case
        if (splitCommit.getCreationTime() == givenCommit.getCreationTime()) {
            System.out.println(ANCESTOR_BRANCH_MSG);
            return true;
        }
        // Fast forward case
        if (splitCommit.getCreationTime() == currCommit.getCreationTime()) {
            currBranch.setCommitId(givenBranch.getCommitId());
            System.out.println(FAST_FORWARD_MSG);
            return true;
        }
        // Get all tracked files in all three branches
        Set<String> currFiles = currCommit.getAllFileIds().keySet();
        Set<String> givenFiles = givenCommit.getAllFileIds().keySet();
        Set<String> splitFiles = splitCommit.getAllFileIds().keySet();
        // Find mutual files
        ArrayList<String> mutualFiles = findMutualFiles(currFiles, givenFiles, splitFiles);
        boolean mutualSuccess = processMutuals(mutualFiles, currCommit, givenCommit, splitCommit);
        boolean discreteSuccess;
        discreteSuccess = processDiscretes(mutualFiles, currCommit, givenCommit, splitCommit);
        if (!mutualSuccess | !discreteSuccess) {
            return false;
        }
        if (conflictExist) {
            // Print merge conflict message
            System.out.println(CONFLICT_MSG);
            return true;
        } else {
            // Commit the merge files
            String msg = "Merged " + head + " with " + givenBranchName + ".";
            String[] inputArgs = {"commit", msg};
            new OptCommit(inputArgs).execute();
            // Print non-conflict merge message
            return true;
        }
    }

    private boolean processMutuals(ArrayList<String> mutuals, Commit cC, Commit gC, Commit sC) {
        if (mutuals.isEmpty()) {
            return true;
        }

        for (String filename : mutuals) {
            String localPath = FileController.getCurrentPath() + "/" + filename;
            String currId = cC.getFileId(filename);
            String givenId = gC.getFileId(filename);
            String splitId = sC.getFileId(filename);

            if (splitId.equals(currId) && splitId.equals(givenId)) {
                // SPLIT == CURR == GIVEN
                if (!(FileController.overwriteFiles(localPath, currId))) {
                    return false;
                }
            } else if (splitId.equals(currId)) {
                // SPLIT == CURR != GIVEN
                FileController.overwriteFiles(localPath, givenId);

                // Stage the given file
                String[] inputArgs = {"add", filename};
                if (!(new OptAdd(inputArgs).execute())) {
                    return false;
                }
            } else if (splitId.equals(givenId)) {
                // SPLIT == GIVEN != CURR
                if (!(FileController.overwriteFiles(localPath, currId))) {
                    return false;
                }
            } else {
                // CURR == GIVEN != SPLIT
                if (currId.equals(givenId)) {
                    if (!(FileController.overwriteFiles(localPath, currId))) {
                        return false;
                    }
                } else {
                    // CURR != GIVEN != SPLIT
                    if (!(FileController.mergeFiles(currId, givenId, localPath))) {
                        return false;
                    }
                    conflictExist = true;
                }
            }
        }

        return true;
    }

    private boolean processDiscretes(ArrayList<String> mutuals, Commit cC, Commit gC, Commit sC) {
        // Get all tracked files in all three branches
        Set<String> currFiles = cC.getAllFileIds().keySet();
        Set<String> givenFiles = gC.getAllFileIds().keySet();
        Set<String> splitFiles = sC.getAllFileIds().keySet();

        // Exclude mutual files in diff branches
        if (!mutuals.isEmpty()) {
            for (String filename : mutuals) {
                currFiles.remove(filename);
                givenFiles.remove(filename);
                splitFiles.remove(filename);
            }
        }

        if (splitFiles.isEmpty() && currFiles.isEmpty() && givenFiles.isEmpty()) {
            return true;
        }

        if (splitFiles.isEmpty()) {
            if (!currGivenCompare(cC, gC, currFiles, givenFiles)) {
                return false;
            }
        } else {

            for (String filename : splitFiles) {
                if (currFiles.contains(filename)) {
                    // curr = split, absent in given
                    if (sC.getFileId(filename).equals(cC.getFileId(filename))) {

                        String localPath = FileController.getCurrentPath() + "/" + filename;
                        Utils.restrictedDelete(localPath);

//                        String[] inputArgs = {"rm", filename};
//                        if (!(new OptRemove(inputArgs).execute())) {
//                            return false;
//                        }

                    } else {
                        // curr != split, absent in given
                        if (!writeLocalConflict(filename, cC, null)) {
                            return false;
                        }
                    }

                    currFiles.remove(filename);
                }

                if (givenFiles.contains(filename)) {
                    // given = split, absent in curr
                    if (sC.getFileId(filename).equals(gC.getFileId(filename))) {
                        String localPath = FileController.getCurrentPath() + "/" + filename;
                        Utils.restrictedDelete(localPath);
                    } else {
                        // given != split, absent in curr
                        if (!writeLocalConflict(filename, null, gC)) {
                            return false;
                        }
                    }

                    givenFiles.remove(filename);
                }
            }

            if (!currGivenCompare(cC, gC, currFiles, givenFiles)) {
                return false;
            }

        }

        return true;
    }

    private boolean currGivenCompare(Commit cC, Commit gC, Set<String> cF, Set<String> gF) {

        if (cF.isEmpty()) {
            // In given only
            for (String filename : gF) {
                if (!writeLocalGiven(filename, gC)) {
                    return false;
                }
            }
        } else if (gF.isEmpty()) {
            // In curr only
            for (String filename : cF) {
                if (!writeLocalCurr(filename, cC)) {
                    return false;
                }
            }
        } else {
            for (String filename : cF) {
                // In curr only
                if (!gF.contains(filename)) {
                    if (!writeLocalCurr(filename, cC)) {
                        return false;
                    }
                } else {
                    if (cC.getFileId(filename).equals(gC.getFileId(filename))) {
                        // In curr and given, same version
                        if (!writeLocalCurr(filename, cC)) {
                            return false;
                        }
                    } else {
                        // In curr and given, diff version
                        if (!writeLocalConflict(filename, cC, gC)) {
                            return false;
                        }
                    }
                }
            }

            for (String filename : gF) {
                // In given only
                if (!cF.contains(filename)) {
                    if (!writeLocalGiven(filename, gC)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean writeLocalCurr(String filename, Commit currCommit) {
        String localPath = FileController.getCurrentPath() + "/" + filename;
        String currId = currCommit.getFileId(filename);

        if (!(FileController.overwriteFiles(localPath, currId))) {
            return false;
        }

        return true;
    }

    private boolean writeLocalGiven(String filename, Commit givenCommit) {
        String localPath = FileController.getCurrentPath() + "/" + filename;
        String givenId = givenCommit.getFileId(filename);

        FileController.overwriteFiles(localPath, givenId);

        // Stage the given file
        String[] inputArgs = {"add", filename};
        if (!(new OptAdd(inputArgs).execute())) {
            return false;
        }

        return true;
    }

    private boolean writeLocalConflict(String filename, Commit cC, Commit gC) {
        String localPath = FileController.getCurrentPath() + "/" + filename;
        String currId;
        String givenId;
        if (cC != null) {
            currId = cC.getFileId(filename);
        } else {
            currId = null;
        }
        if (gC != null) {
            givenId = gC.getFileId(filename);
        } else {
            givenId = null;
        }

        if (!(FileController.mergeFiles(currId, givenId, localPath))) {
            return false;
        }
        conflictExist = true;
        return true;
    }

    private ArrayList<String> findMutualFiles(Set<String> cF, Set<String> gF, Set<String> sF) {
        ArrayList<String> mutualFileList = new ArrayList<>();

        // Filter out mutual files
        if (!sF.isEmpty()) {
            for (String filename : sF) {
                if (cF.contains(filename) && gF.contains(filename)) {
                    mutualFileList.add(filename);
                }
            }
        }

        return mutualFileList;
    }

}
