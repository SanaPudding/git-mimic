package gitlet;

import java.util.List;

public class OptRemoveBranch implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Incorrect operands.";
    private final String BRANCH_NOT_EXIST_ERR = "A branch with that name does not exist.";
    private final String HEAD_BRANCH_ERR = "Cannot remove the current branch.";

    public OptRemoveBranch(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length != 2) {
            System.out.println(ARGS_ERR);
            return false;
        }

        // Check if the given branch exist
        List<String> branches = Branch.getAllName();
        if (branches.contains(args[1])) {
            System.out.println(BRANCH_NOT_EXIST_ERR);
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
        String branchName = args[1];

        // Read HEAD pointer
        String head = FileController.getHEAD();
        if (head == null) {
            return false;
        }

        // Check if the given branch is HEAD branch
        if (branchName.equals(head)) {
            System.out.println(HEAD_BRANCH_ERR);
            return false;
        }

        // Generate branch path
        String branchPath = FileController.getBranchPath(branchName);

        // Delete the given branch from local
        return Utils.restrictedDelete(branchPath);
    }

}
