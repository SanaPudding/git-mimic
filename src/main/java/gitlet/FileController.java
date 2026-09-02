package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.List;
import java.io.OutputStreamWriter;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectOutput;
import java.io.ObjectInputStream;

public class FileController {

    /**
     * Get different kinds of file paths.
     * */

    //Current Path.
    static String getCurrentPath() {
        return System.getProperty("user.dir");
    }

    //Current Head.
    static String getHEAD() {
        String path = getCurrentPath() + "/.gitlet/HEAD/HEAD";
        return readContentsString(path);
    }

    //Current Commit Path.
    static String getCommitPath() {
        return getCurrentPath() + "/.gitlet/commit";
    }

    //Current Stage Path
    static String getStagePath() {
        return getCurrentPath() + "/.gitlet/stage/stage";
    }

    //Current Branch Path
    static String getBranchPath(String name) {
        return getCurrentPath() + "/.gitlet/branch/" + name;
    }


    /**
     * Create files, directory and SHA1 id.
     * */

    //Create files/directory.
    static void createNewFile(String name, String contents) throws IOException {
        File newFile = new File(getCurrentPath() + "/" + name);
        if (!newFile.exists()) {
            newFile.createNewFile();
            if (contents != null) {
                FileOutputStream fos = new FileOutputStream(getCurrentPath() + "/" + name);
                fos.write(contents.getBytes());
                fos.flush();
                fos.close();
            }
        }
    }

    //Create new file directory.
    static void createNewRepo(String name) {
        File newRepo = new File(getCurrentPath() + "/" + name);
        if (!newRepo.exists()) {
            newRepo.mkdir();
        }
    }

    //Create SHA1 id for objects.
    static String createObjectId(Object obj) {
        try {
            return convertToId(convertToBytes(obj));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Read files and return diffrent types of objects.
     * */

    //Read a file and return its content in SHA1 id.
    static String createFileId(String path) {
        try {
            String content = readContentsString(path);
            return convertToId(convertToBytes(content));
        } catch (IOException e) {
            return null;
        }
    }

    //Read all filenames in working directory
    static List<String> readAllLocalFileNames() {
        return Utils.plainFilenamesIn(getCurrentPath());
    }

    //Read file and return its content in byte arrays.
    static byte[] readContentsByte(String path) {
        try {
            FileInputStream input = new FileInputStream(path);
            byte[] cont = input.readAllBytes();
            return cont;
        } catch (IOException e) {
            return null;
        }
    }

    //Read file and return its content in String.
    static String readContentsString(String path) {
        String ret = new String(readContentsByte((path).toString()));
        return ret;
    }

    /**
     * Write and store files.
     * */

    //Write files
    static boolean writeFile(String path, String content) {
        File file = new File(path);
        Utils.writeContents(file, content.getBytes());
        return true;
    }

    //Overwrite files, used in checkout and merge
    static boolean overwriteFiles(String path, String id) {
        try {
            String dirname = id.substring(0, 2);
            String fpath = ".gitlet/object/" + dirname + "/";
            byte[] cont = readContentsByte(fpath + id);
            File overwriteFile = new File(path);
            overwriteFile.createNewFile();
            Utils.writeContents(overwriteFile, cont);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //Append files
    static boolean appendFiles(String path, String content) {
        try {
            FileOutputStream fos = new FileOutputStream(path, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            String newline = System.getProperty("line.separator");
            osw.write(newline);
            osw.write(content);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static boolean mergeFiles(String currid, String otherid, String path) {
        if (currid != null) {
            String currCont = readObject(currid);
            if (otherid != null) {
                String otherCont = readObject(otherid);
                String lineSeparator = System.getProperty("line.separator");
                String mergeCont = "<<<<<<< HEAD" + lineSeparator;
                mergeCont += currCont;
                mergeCont += "=======" + lineSeparator;
                mergeCont += otherCont;
                mergeCont += ">>>>>>>" + lineSeparator;
                writeFile(path, mergeCont);
            } else {
                String otherCont = "";
                String lineSeparator = System.getProperty("line.separator");
                String mergeCont = "<<<<<<< HEAD" + lineSeparator;
                mergeCont += currCont;
                mergeCont += "=======" + lineSeparator;
                mergeCont += otherCont;
                mergeCont += ">>>>>>>" + lineSeparator;
                writeFile(path, mergeCont);
            }
        } else {
            String currCont = "";
            if (otherid != null) {
                String otherCont = readObject(otherid);
                String lineSeparator = System.getProperty("line.separator");
                String mergeCont = "<<<<<<< HEAD" + lineSeparator;
                mergeCont += currCont;
                mergeCont += "=======" + lineSeparator;
                mergeCont += otherCont;
                mergeCont += ">>>>>>>" + lineSeparator;
                writeFile(path, mergeCont);
            } else {
                String otherCont = "";
                String lineSeparator = System.getProperty("line.separator");
                String mergeCont = "<<<<<<< HEAD" + lineSeparator;
                mergeCont += currCont;
                mergeCont += "=======" + lineSeparator;
                mergeCont += otherCont;
                mergeCont += ">>>>>>>" + lineSeparator;
                writeFile(path, mergeCont);
            }
        }
        return true;
    }

    //Store Objects
    static boolean storeObject(Object obj, String type, String id) {
        try {
            if (type.equals("commit")) {
//                String dirname = id.substring(0, 2);
//                File newdir = new File(getCommitPath() + dirname);
//                newdir.mkdir();
                String path = ".gitlet/commit/" + id;
                File newcommit = new File(path);
                newcommit.createNewFile();
                serialization(obj, path);
            } else if (type.equals("stage")) {
                File stage = new File(getStagePath());
                stage.createNewFile();
                serialization(obj, getStagePath());
            } else if (type.equals("branch")) {
                File newbranch = new File(getBranchPath(id));
                newbranch.createNewFile();
                serialization(obj, getBranchPath(id));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static boolean storeFiles(String path, String id) {
        try {
            byte[] cont = readContentsByte(path);
            File file = new File(".gitlet/object");
            if (file != null) {
                String dirname = id.substring(0, 2);
                File newdir = new File(".gitlet/object/" + dirname);
                newdir.mkdir();
                String fpath = ".gitlet/object/" + dirname + "/";
                File copyfile = new File(fpath + id);
                copyfile.createNewFile();
                Utils.writeContents(copyfile, cont);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static boolean storeStage(Stage stage) {
        return storeObject(stage, "stage", null);
    }

    /**
     * Read files.
     * */

    static Stage readStage() {
        try {
            String path = getStagePath();
            Stage deStage = (Stage) deserialization(path);
            return deStage;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    static Branch readBranch(String name) {
        try {
            String path = getBranchPath(name);
            Branch deBranch = (Branch) deserialization(path);
            return deBranch;
        } catch (ClassNotFoundException a) {
            return null;
        } catch (IOException b) {
            return null;
        }
    }

    static Commit readCommit(String id) {
        try {
            String path = getCommitPath() + "/" + id;
            Commit deCommit = (Commit) deserialization(path);
            return deCommit;
        } catch (ClassNotFoundException a) {
            return null;
        } catch (IOException b) {
            return null;
        }
    }

    static String readObject(String id) {
        String dirname = id.substring(0, 2);
        String path =  ".gitlet/object/" + dirname + "/" + id;
        String reS = FileController.readContentsString(path);
        return reS;
    }

    static String findCommit(String commitId) {
        File commitDir = new File(getCommitPath());
        if (!commitDir.exists() | !commitDir.isDirectory()) {
            return null;
        }

        CommitFilter cmtFilter = new CommitFilter(commitId);
        String[] nameFounded = commitDir.list(cmtFilter);
        if (nameFounded == null) {
            return null;
        }
        if (nameFounded.length == 0 | nameFounded.length > 1) {
            return null;
        }
        return nameFounded[0];
    }

    private static class CommitFilter implements FilenameFilter {

        private String prefix;

        CommitFilter(String prefix) {
            this.prefix = prefix;
        }

        public boolean accept(File dir, String name) {
            return name.startsWith(prefix);
        }

    }

    /**
     * Conversion
     * */
    // Turn a byte array into SHA1 id.
    static String convertToId(byte[] bt) {
        return Utils.sha1(bt);
    }

    // Turn an object into a byte array.
    // Note: ALWAYS USE IN TRY/CATCH STRUCTURE OR IT WILL NOT COMPILE!!!
    static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    /**
     * Serialization & Deserialization
     * */
    static void serialization(Object object, String filepath) throws IOException {
        FileOutputStream output = new FileOutputStream(filepath);
        ObjectOutputStream out = new ObjectOutputStream(output);
        out.writeObject(object);
        out.close();
        output.close();
    }

    static Object deserialization(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream input = new FileInputStream(filepath);
        ObjectInputStream in = new ObjectInputStream(input);
        Object reObj = in.readObject();
        in.close();
        input.close();
        return reObj;
    }

}
