package TopN;

import TopN.Utils.FileUtil;
import java.io.File;
import org.junit.Test;

public class TestFileUtil {
    @Test
    public void testFileSplit() {
        try {
            String targetPath = "D:\\soucecode\\data";
            File file = new File("D:\\soucecode\\data\\data.txt");
            FileUtil fileUtil = new FileUtil();
            fileUtil.splitFile(file, targetPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadSplitFileToWC() {
        try {
            String sourcePath = "D:\\soucecode\\data";
            String targetPath = "D:\\soucecode\\data";
            File sourceFile = new File(sourcePath);
            File targetFile = new File(targetPath);
            FileUtil fileUtil = new FileUtil();
            fileUtil.readSplitFileToWC(sourceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileMerge() {
        try {
            String sourcePath = "D:\\soucecode\\data";
            String targetPath = "D:\\soucecode\\data";
            FileUtil fileUtil = new FileUtil();
            File sourceFile = new File(sourcePath);
            File targetFile = new File(targetPath);
            fileUtil.mergeFile(sourceFile, targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
