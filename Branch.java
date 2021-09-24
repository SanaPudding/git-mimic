package gitlet;

import java.io.Serializable;
import java.util.List;

public class Branch implements Serializable {

    private String name;
    private String commitId;

    public Branch(String name, String commitId) {
        this.name = name;
        this.commitId = commitId;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCommitId() {
        return commitId;
    }

    public static List<String> getAllName() {
        return Utils.plainFilenamesIn(FileController.getBranchPath(""));
    }

    // Setters
    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

//    private ArrayList<String> splitList;

//    this.splitList = new ArrayList<>();
//    this.splitList.addAll(inList);

//    public ArrayList<String> getSplitList() {
//        return splitList;
//    }
//
//    public int getSplitLength() {
//        return splitList.size();
//    }
//
//    // Setter
//    public void addSplit(String splitNodeId) {
//        splitList.add(splitNodeId);
//    }

    // Utils

}
