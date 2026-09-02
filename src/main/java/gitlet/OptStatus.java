package gitlet;

import java.util.List;
import java.util.Map;

public class OptStatus implements Operation {

    private String[] args;
    private final String ARGS_ERR = "Incorrect operands.";

    private final String HEADER_BRANCH = "=== Branches ===";
    private final String HEADER_ADDED = "=== Staged Files ===";
    private final String HEADER_REMOVED = "=== Removed Files ===";
    private final String HEADER_MODIFIED = "=== Modifications Not Staged For Commit ===";
    private final String HEADER_UNTRACKED = "=== Untracked Files ===";

    public OptStatus(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isValidArgs() {
        if (args.length != 1) {
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

        /**
         * Branch name section
         * */

        // Read HEAD pointer
        String head = FileController.getHEAD();
        if (head == null) {
            return false;
        }

        // Read all branch names
        List<String> branchNameList = Branch.getAllName();

        // Check for inclusion
        if (!branchNameList.contains(head)) {
            return false;
        }

        // Exclude current branch from the list
        branchNameList.remove(head);

        // Print branch names
        System.out.println(HEADER_BRANCH);
        System.out.println("*" + head);
        if (!branchNameList.isEmpty()) {
            for (String name : branchNameList) {
                System.out.println(name);
            }
        }
        System.out.println();

        /**
         * Added/Removed filename section
         * */

        // Read staging area
        Stage stage = FileController.readStage();
        if (stage == null) {
            return false;
        }

        // Get all stage info
        Map<String, String> addMaps = stage.getAllAdds();
        Map<String, String> removeMaps = stage.getAllRemoves();

        // Print added files
        System.out.println(HEADER_ADDED);
        if (!addMaps.isEmpty()) {
            for (String name : addMaps.keySet()) {
                System.out.println(name);
            }
        }
        System.out.println();

        // Print removed files
        System.out.println(HEADER_REMOVED);
        if (!removeMaps.isEmpty()) {
            for (String name : removeMaps.keySet()) {
                System.out.println(name);
            }
        }
        System.out.println();

        /**
         * Modified and Untracked filename section
         * */

        // Print headers
        System.out.println(HEADER_MODIFIED);
        System.out.println();
        System.out.println(HEADER_UNTRACKED);
        System.out.println();

        return true;
    }

}
