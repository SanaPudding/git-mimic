package gitlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmallTest {

    public static void testDeserialization() throws IOException, ClassNotFoundException {
        String filepath = "fbd064eb975c1c23cc85301c122f0690f720d1ed";
        Commit testcomit = (Commit) FileController.deserialization(filepath);
        //System.out.println(testcomit.getMessage());
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        try {
            testDeserialization();
        } catch (IOException a) {
            System.out.println("failed");
        } catch (ClassNotFoundException b) {
            System.out.println("failed");
        }
//        String workdir = FileController.getCurrentPath() + "/.gitlet";
//        List<String> workingDir = Utils.plainFilenamesIn(workdir);
//        System.out.println(workingDir == null);
//        for (String a : workingDir) {
//            System.out.println(a);
//        }
//        Commit testCommit = new Commit("Test Serialization", null);
//        String commitId = FileController.createObjectId(testCommit);
//        System.out.println(commitId);
//        FileController.storeObject(testCommit,"commit",commitId);
//        String path = FileController.getCurrentPath() + "/" + "contentTest";
//        System.out.println(path);
//        File contentTest = new File(path);
//        System.out.println(contentTest.exists());
//        System.out.println(FileController.createFileId(path));
//        File test = new File(".gitlet/HEAD/HEAD.txt");
//        System.out.println(test.exists());
//        String filepaath = FileController.getCurrentPath() + "/.gitlet/HEAD/HEAD.txt";
//        System.out.println(FileController.readFile(filepaath));
//        String writefilepath = FileController.getCurrentPath() + "/.gitlet/HEAD/HEAD.txt";
//        FileController.writeFile(writefilepath, objId);
//        String filepaaath = FileController.getCurrentPath() + "/.gitlet/HEAD/HEAD.txt"
//        System.out.println(FileController.readFile(filepaaath));
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        String time = dtf.format(now);
//        String path = ".gitlet/HEAD/HEAD.txt";
//        String content = "this is the head pointer file.\n";
//        FileController.createNewFile(path, content);
//        byte[] fileContent = FileController.readContents(path);
//        System.out.println(fileContent);
//        ArrayList<String> testList = new ArrayList<>();
//        testList.add("1");
//        testList.add("2");
//        testList.add("3");
//        testList.add("4");
//        ArrayList<String> toList = new ArrayList<>();
//        toList.addAll(testList);
//        System.out.println(toList.get(0));
//        System.out.println(toList.get(1));
//        System.out.println(toList.get(2));
//        System.out.println(toList.get(3));
//        Commit cmtTest = new Commit("hello", "123", "456");
//        Map<String, String> testMap = new HashMap<>();
//        testMap.put("a", "hello");
//        testMap.put("b", "hello Sandy");
//        testMap.put("c", "hello Sue");
//        testMap.put("d", "hello Tom");
//        cmtTest.setAllId(testMap);
//        System.out.println(cmtTest.getId("a"));
//        System.out.println(cmtTest.getId("b"));
//        System.out.println(cmtTest.getId("c"));
//        System.out.println(cmtTest.getId("d"));
//        System.out.println(cmtTest.getId("e"));

//        System.out.println(System.getProperty("user.dir") + "/.gitlet");
//        File test = new File("./gitlet/testRepo");
//
//        System.out.println(test.listFiles()[0]);
//        System.out.println(test.listFiles()[1]);
//        System.out.println(test.listFiles()[2]);
//        System.out.println(test.listFiles()[3]);
//
//        if (test.exists()) {
//            System.out.println(true);
//        } else {
//            System.out.println(false);
//        }

    }

}
