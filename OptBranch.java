package gitlet;

import java.util.List;

public class OptBranch implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Incorrect operands.";
    private final String BRANCH_EXIST_ERR = "A branch with that name already exists.";

    public OptBranch(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length != 2) {
            System.out.println(ARGS_ERR);
            return false;
        }

        // Check if the given branch already created;
        List<String> branches = Branch.getAllName();
        if (branches.contains(args[1])) {
            System.out.println(BRANCH_EXIST_ERR);
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

        // Read current branch
        Branch currBranch = FileController.readBranch(head);
        if (currBranch == null) {
            return false;
        }

        // Create new branch
        Branch newBranch = new Branch(branchName, currBranch.getCommitId());

        // Store created branch
        return FileController.storeObject(newBranch, "branch", branchName);

    }

}
