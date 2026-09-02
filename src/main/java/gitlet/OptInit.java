package gitlet;

import java.io.File;
import java.io.IOException;

public class OptInit implements Operation {

    private String[] args;

    private String currPath = System.getProperty("user.dir") + "/.gitlet";
    private final String EXIST_ERR;
    private final String ARGS_ERR = "Incorrect operands.";

    public OptInit(String[] args) {
        this.args = args;
        EXIST_ERR = "A gitlet version-control system already exists in the current directory.";
    }

    @Override
    public boolean isValidArgs() {
        return args.length == 1;
    }

    @Override
    public boolean execute() {
        if (!isValidArgs()) {
            System.out.println(ARGS_ERR);
            return false;
        }

        File gitletRepo = new File(currPath);
        if (gitletRepo.exists() && gitletRepo.isDirectory()) {
            System.out.println(EXIST_ERR);
            return false;
        } else {
            FileController.createNewRepo(".gitlet");
            FileController.createNewRepo(".gitlet/HEAD");
            FileController.createNewRepo(".gitlet/branch");
            FileController.createNewRepo(".gitlet/stage");
            FileController.createNewRepo(".gitlet/commit");
            FileController.createNewRepo(".gitlet/object");
            //FileController.createNewRepo(".gitlet/bolbs");

            // Initialize initial commit
            Commit initCommit = new Commit("initial commit", "root");

            // Generate initCommit's Id
            String initCommitId = FileController.createObjectId(initCommit);

            // Initialize master branch
            Branch master = new Branch("master", initCommitId);

            // Assign master branch's name to HEAD
            String head = master.getName();

            // Store HEAD as file to local
            if (head == null) {
                return false;
            } else {
                try {
                    FileController.createNewFile(".gitlet/HEAD/HEAD", head);
                } catch (IOException e) {
                    return false;
                }
            }

            // Store initCommit id to local
            try {
                FileController.createNewFile(".gitlet/HEAD/InitCommitId", initCommitId);
            } catch (IOException e) {
                return false;
            }

            // Initialize stage
            Stage stage = new Stage();

            // Generate stages' Id
            String stageId = FileController.createObjectId(stage);
            if (stageId == null) {
                return false;
            }

            // Store initCommit, master branch, and stage to local

            return FileController.storeObject(initCommit, "commit", initCommitId)
                    && FileController.storeObject(master, "branch", head)
                    && FileController.storeObject(stage, "stage", null);
        }

    }

}
