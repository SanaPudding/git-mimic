package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

public class Commit implements Serializable {

    private Map<String, String> fileIds;

    private String date;
    private String msg;

    private String parentId;
    private ArrayList<String> children;

    private Long timeCreated;

    public Commit(String msg, String parentId) {
        this.fileIds = new HashMap<>();
        this.children = new ArrayList<>();
        this.date = Utils.getCurrentTime();
        this.msg = msg;
        this.parentId = parentId;
        this.timeCreated = System.currentTimeMillis();
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }

    public String getFileId(String filename) {
        return fileIds.get(filename);
    }

    public String getParentId() {
        return parentId;
    }

    public String getChildAt(int index) {
        return children.get(index);
    }

    public ArrayList<String> getAllChild() {
        return children;
    }

    public Map<String, String> getAllFileIds() {
        return fileIds;
    }

    public long getCreationTime() {
        return timeCreated;
    }

    public int getFileCount() {
        return fileIds.size();
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isTracked(String filename) {
        return fileIds.containsKey(filename);
    }

    // Setters
    public void replaceFileId(String filename, String newId) {
        fileIds.replace(filename, newId);
    }

    public void addFileId(String filename, String fileId) {
        fileIds.put(filename, fileId);
    }

    public void addAllFileId(Map<String, String> inMap) {
        fileIds.putAll(inMap);
    }

    public void addChild(String commitId) {
        children.add(commitId);
    }

    // Remove methods
    public void removeFileId(String filename) {
        fileIds.remove(filename);
    }

    // Helper function that finds the split point of two given commit
    public static Commit findSplitPoint(Commit A, Commit B) {
        if (A.getCreationTime() == B.getCreationTime()) {
            return A;
        }
        if (A.getCreationTime() < B.getCreationTime()) {
            Commit parentB = FileController.readCommit(B.getParentId());
            return findSplitPoint(A, parentB);
        } else {
            Commit parentA = FileController.readCommit(A.getParentId());
            return findSplitPoint(parentA, B);
        }
    }

    // Helper function checks for untracked files overwrite
    public static boolean noOverwriteErr(Commit currCommit, Commit givenCommit) {
        // Read all local file names
        List<String> localFiles = FileController.readAllLocalFileNames();
        if (localFiles.isEmpty()) {
            return true;
        }

        // Get all tracked files of current commit
        Set<String> currFiles = currCommit.getAllFileIds().keySet();

        // Generate untracked files list of current commit
        ArrayList<String> currUntrackedFiles = new ArrayList<>();

        // Filter out untracked files of current commit
        for (String localFile : localFiles) {
            if (!currFiles.contains(localFile)) {
                currUntrackedFiles.add(localFile);
            }
        }

        // Check if there is any untracked files
        if (currUntrackedFiles.isEmpty()) {
            return true;
        }

        // Get all tracked files of given commit
        Set<String> givenFiles = givenCommit.getAllFileIds().keySet();
        if (givenFiles.isEmpty()) {
            return true;
        }

        // Check possible overwritten files
        for (String filename : currUntrackedFiles) {
            if (givenFiles.contains(filename)) {
                String localPath = FileController.getCurrentPath() + "/" + filename;
                String localFileId = FileController.createFileId(localPath);
                if (!givenCommit.getFileId(filename).equals(localFileId)) {
                    String error = "There is an untracked file in the way ";
                    error += "delete it or add it first.";
                    System.out.println(error);
                    return false;
                }
            }
        }

        return true;
    }

}
