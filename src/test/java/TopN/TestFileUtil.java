package TopN;

import Utils.FileUtil;
import org.junit.Test;

import java.io.File;

public class TestFileUtil {
    @Test
    public void testFileSplit() {
        try {
            /*String targetPath = "D:\\soucecode\\data";
            File file = new File("D:\\soucecode\\data\\data.txt");*/
            String targetPath = "D:\\data2\\s0";
            File file = new File("D:\\data2\\0.part");
            FileUtil fileUtil = new FileUtil();
            fileUtil.splitFile(file, targetPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadSplitFileToWC() {
        try {
           /* String sourcePath = "D:\\soucecode\\data";
            String targetPath = "D:\\soucecode\\data";*/
            String sourcePath = "D:\\data2\\s0";
            String targetPath = "D:\\data2\\s1";
            File sourceFile = new File(sourcePath);
            File targetFile = new File(targetPath);
            FileUtil fileUtil = new FileUtil();
            fileUtil.readSplitFileToWC(sourceFile, targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileMerge() {
        try {
            /*String sourcePath = "D:\\soucecode\\data";
            String targetPath = "D:\\soucecode\\data\\cs";*/
            String sourcePath = "D:\\data2\\s1";
            String targetPath = "D:\\data2\\s2";
            FileUtil fileUtil = new FileUtil();
            File sourceFile = new File(sourcePath);
            File targetFile = new File(targetPath);
            fileUtil.mergeFile(sourceFile, targetFile, "data.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
