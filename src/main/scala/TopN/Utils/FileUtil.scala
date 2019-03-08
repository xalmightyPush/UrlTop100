//package TopN.Utils
//
//import java.io.File
//import java.io.FilenameFilter
//import java.io.IOException
//import java.io.SequenceInputStream
//import java.text.DecimalFormat
//import java.{lang, util}
//import java.util.{Collections, Comparator, Date, Properties}
//
//import org.apache.commons.io.FileUtils
//import org.apache.commons.lang.time.FastDateFormat
//import org.apache.commons.logging.LogFactory
//import java.io.FileInputStream
//import java.io.FileOutputStream
//
//import org.mapdb.{DB, DBMaker, HTreeMap, Serializer}
//
//import scala.collection.JavaConversions._
//import scala.util.control.Breaks._
//
//object FileUtil { // 定义单个文件的大小这里采用800m
//    private val SIZE = 1024 * 1024 * 800
//    var currentWorkDir: String = System.getProperty("user.dir") + "\\"
//}
//
//class FileUtil() {
//    val log = LogFactory.getLog(this.getClass)
//    private val fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS")
//    private var begin = 0L
//    private var end = 0L
//    private var zxsj = 0L
//    private val hashUtils = HashUtils.MurMurHash
//
//    /**
//      * 功能说明：合并文件
//      *
//      * @param dir
//      * @throws Exception
//      */
//    @throws[Exception]
//    def mergeFile(dir: File): Unit = {
//        val curDate = new Date(System.currentTimeMillis)
//        val strCurrTime = fastDateFormat.format(curDate)
//        begin = new Date().getTime
//        // 读取properties文件的拆分信息
//        val files = dir.listFiles(new FilenameFilter() {
//            override def accept(dir: File, name: String): Boolean = name.endsWith(".s2")
//        })
//        val file = files(0)
//        // 获取该文件的信息
//        val pro = new Properties
//        val fis = new FileInputStream(file)
//        pro.load(fis)
//        val fileName = pro.getProperty("fileName")
//        val splitCount = Integer.valueOf(pro.getProperty("partCount"))
//        if (files.length != 1) throw new Exception(dir + ",该目录下没有解析的properties文件或不唯一")
//        // 获取该目录下所有的碎片文件
//        val partFiles = dir.listFiles(new FilenameFilter() {
//            override def accept(dir: File, name: String): Boolean = name.endsWith(".sort")
//        })
//        // 将碎片文件存入到集合中
//        val al = new util.ArrayList[FileInputStream]
//        var i = 0
//        while ( {
//            i < splitCount
//        }) {
//            try
//                al.add(new FileInputStream(partFiles(i)))
//            catch {
//                case e: Exception =>
//                    // 异常
//                    e.printStackTrace()
//            }
//            i += 1
//            i - 1
//        }
//        try { // 构建文件流集合
//            val en = Collections.enumeration(al)
//            // 将多个流合成序列流
//            val sis = new SequenceInputStream(en)
//            val fos = new FileOutputStream(new File(dir, fileName))
//            val b = new Array[Byte](FileUtil.SIZE)
//            var len = 0
//            while ((len = sis.read(b)) != -1) fos.write(b, 0, len)
//            fos.close()
//            sis.close()
//            fis.close()
//            end = new Date().getTime
//            zxsj = (end - begin) / 1000
//            log.info("当前时间为:" + strCurrTime + " 执行时间为: " + zxsj + " s")
//        } catch {
//            case e: Exception =>
//                e.printStackTrace()
//        }
//    }
//
//    /**
//      * 功能说明：拆分文件
//      *
//      * @param file
//      * @param targetPath
//      */
//    def splitFile(file: File, targetPath: String): Unit = {
//        try {
//            logMemory()
//            val curDate = new Date(System.currentTimeMillis)
//            val strCurrTime = fastDateFormat.format(curDate)
//            begin = new Date().getTime
//            val fileSize = GetFileSize(file)
//            var splitCount = GetFileNums(file)
//            log.info("The file's Size:" + fileSize + " spilt file's num:" + splitCount)
//            /**
//              * 切割文件时，记录 切割文件的名称和切割的子文件个数以方便合并
//              * 这个信息为了简单描述，使用键值对的方式，用到了properties对象
//              */
//            val pro = new Properties
//            // 定义输出的文件夹路径
//            val dir = new File(targetPath)
//            // 判断文件夹是否存在，不存在则创建
//            if (!dir.exists) dir.mkdirs
//            val al = new util.ArrayList[FileOutputStream]
//            var i = 0
//            while (i < splitCount) {
//                try
//                    al.add(new FileOutputStream(new File(dir, i + ".part")))
//                catch {
//                    case e: Exception =>
//                        e.printStackTrace()
//                }
//                i += 1
//                i - 1
//            }
//            // 切割文件
//            try {
//                val it = FileUtils.lineIterator(file, "UTF-8")
//                try {
//                    while (it.hasNext) {
//                        val line = it.nextLine
//                        val target_file_name = (hashUtils.hash(line) % (splitCount - 1)).toInt
//                        al.get(target_file_name).write(line.getBytes)
//                        al.get(target_file_name).write("\n".getBytes)
//                        al.get(target_file_name).flush()
//                    }
//                    for (stream <- al) {
//                        stream.close()
//                    }
//                } catch {
//                    case e: IOException =>
//                        throw new RuntimeException(e.getMessage, e)
//                } finally if (it != null) it.close()
//            }
//            end = new Date().getTime
//            zxsj = (end - begin) / 1000
//            log.info("当前时间为:" + strCurrTime + " 执行时间为: " + zxsj + " s")
//            // 将被切割的文件信息保存到properties中
//            pro.setProperty("partCount", splitCount + "")
//            pro.setProperty("fileName", file.getName)
//            pro.setProperty("currTime", strCurrTime)
//            pro.setProperty("zxsj", zxsj.toString)
//            val fo = new FileOutputStream(new File(dir, {
//                splitCount += 1
//                splitCount - 1
//            } + ".s1"))
//            // 写入properties文件
//            pro.store(fo, "save file info")
//            fo.close()
//            logMemory()
//        } catch {
//            case e: Exception =>
//                e.printStackTrace()
//        }
//    }
//
//    /**
//      * 功能说明：按文件大小均匀拆分文件
//      *
//      * @param file
//      * @param targetPath
//      */
//    def splitFileBySize(file: File, targetPath: String): Unit = {
//        try {
//            logMemory()
//            val curDate = new Date(System.currentTimeMillis)
//            val strCurrTime = fastDateFormat.format(curDate)
//            begin = new Date().getTime
//            val fileSize = GetFileSize(file)
//            val splitCount = GetFileNums(file)
//            log.info("The file's Size:" + fileSize + " spilt file's num:" + splitCount)
//            val fs = new FileInputStream(file)
//            // 定义缓冲区
//            val b = new Array[Byte](FileUtil.SIZE)
//            var fo: FileOutputStream = null
//            var len = 0
//            var count = 0
//            /**
//              * 切割文件时，记录 切割文件的名称和切割的子文件个数以方便合并
//              * 这个信息为了简单描述，使用键值对的方式，用到了properties对象
//              */
//            val pro = new Properties
//            // 定义输出的文件夹路径
//            val dir = new File(targetPath)
//            // 判断文件夹是否存在，不存在则创建
//            if (!dir.exists) dir.mkdirs
//            // 切割文件
//            breakable(
//                while ((len = fs.read(b)) != -1) {
//                    if (count < splitCount) {
//                        fo = new FileOutputStream(new File(dir, {
//                            count += 1
//                            count - 1
//                        } + ".part"))
//                        fo.write(b, 0, len)
//                        fo.close
//                    } else {
//                        break()
//                    }
//                }
//            )
//            // 将被切割的文件信息保存到properties中
//            end = new Date().getTime
//            zxsj = (end - begin) / 1000
//            log.info("当前时间为:" + strCurrTime + " 执行时间为: " + zxsj + " s")
//            // 将被切割的文件信息保存到properties中
//            pro.setProperty("partCount", count + "")
//            pro.setProperty("fileName", file.getName)
//            pro.setProperty("currTime", strCurrTime)
//            pro.setProperty("zxsj", zxsj.toString)
//            fo = new FileOutputStream(new File(dir, {
//                count += 1
//                count - 1
//            } + ".properties"))
//            // 写入properties文件
//            pro.store(fo, "save file info")
//            fs.close()
//            fo.close()
//            logMemory()
//        } catch {
//            case e: Exception =>
//                e.printStackTrace()
//        }
//    }
//
//    @throws[Exception]
//    def readSplitFileToWC(dir: File): Unit = {
//        val db: DB = DBMaker.fileDB("D:\\soucecode\\data\\myDB" + System.currentTimeMillis())
//            .fileMmapEnable()
//            .fileMmapEnableIfSupported()
//            .fileMmapPreclearDisable()
//            .allocateIncrement(1024 * 1024 * 1024)
//            .cleanerHackEnable()
//            .make()
//
//        val curDate = new Date(System.currentTimeMillis)
//        val strCurrTime = fastDateFormat.format(curDate)
//        begin = new Date().getTime
//        val files = dir.listFiles(new FilenameFilter() {
//            override def accept(dir: File, name: String): Boolean = name.endsWith(".s1")
//        })
//        val file = files(0)
//        val pro = new Properties
//        val fis = new FileInputStream(file)
//        pro.load(fis)
//        val fileName = pro.getProperty("fileName")
//        var splitCount = Integer.valueOf(pro.getProperty("partCount"))
//        if (files.length != 1) throw new Exception(dir + ",该目录下没有解析的properties文件或不唯一")
//        val partFiles = dir.listFiles(new FilenameFilter() {
//            override def accept(dir: File, name: String): Boolean = name.endsWith(".part")
//        })
//        //        for (File partFile : partFiles) {
//        try {
//            val map: HTreeMap[java.lang.String, java.lang.Long] = db.hashMap("name_of_map")
//                .keySerializer(Serializer.STRING)
//                .valueSerializer(Serializer.LONG)
//                .create()
//
//            val it = FileUtils.lineIterator(partFiles(0), "UTF-8")
//            try {
//                var count = 0L
//                while (it.hasNext) {
//                    val line = it.nextLine
//                    if (!map.containsKey(line)) {
//                        count = 1L
//                    } else {
//                        count = map.get(line) + 1
//                    }
//                    map.put(line, count)
//                }
//
//                val top100Map = new util.TreeMap[java.lang.Long, java.lang.String](new Comparator[lang.Long] {
//                    override def compare(y: lang.Long, x: lang.Long): Int = return if (x < y) -1 else 1
//                })
//
//                map.foreach(element => {
//                    top100Map.put(element._2, element._1)
//                    if (top100Map.size > 100) { //只保留前面TopN个元素
//                        top100Map.pollLastEntry
//                    }
//                })
//
//                val fo = new FileOutputStream(new File(dir, partFiles(0).getName + ".sort"))
//                top100Map.foreach(e => {
//                    fo.write((e._2 + " " + e._1).getBytes)
//                    fo.write("\n".getBytes)
//                })
//                fo.flush()
//                fo.close
//
//            } catch {
//                case e: IOException =>
//                    throw new RuntimeException(e.getMessage, e)
//            } finally {
//                if (it != null) it.close()
//                db.close()
//            }
//        }
//        //        }
//        fis.close()
//        end = new Date().getTime
//        zxsj = (end - begin) / 1000
//        log.info("当前时间为:" + strCurrTime + " 执行时间为: " + zxsj + " s")
//        // 将被切割的文件信息保存到properties中
//        pro.setProperty("partCount", splitCount + "")
//        pro.setProperty("fileName", file.getName)
//        pro.setProperty("currTime", strCurrTime)
//        pro.setProperty("zxsj", zxsj.toString)
//        val fo = new FileOutputStream(new File(dir, {
//            splitCount += 1
//            splitCount - 1
//        } + ".s2"))
//        // 写入properties文件
//        pro.store(fo, "save file info")
//        fo.close()
//        logMemory()
//    }
//
//    def GetFileSize(file: File): String = {
//        var size = ""
//        if (file.exists && file.isFile) {
//            val fileS = file.length
//            val df = new DecimalFormat("#.00")
//            if (fileS < 1024) size = df.format(fileS.toDouble) + "BT"
//            else if (fileS < 1048576) size = df.format(fileS.toDouble / 1024) + "KB"
//            else if (fileS < 1073741824) size = df.format(fileS.toDouble / 1048576) + "MB"
//            else size = df.format(fileS.toDouble / 1073741824) + "GB"
//        }
//        else if (file.exists && file.isDirectory) size = ""
//        else size = "0BT"
//        size
//    }
//
//    def GetFileNums(file: File): Integer = {
//        var nums = 0.0
//        if (file.exists && file.isFile) {
//            val fileS = file.length
//            nums = Math.ceil(fileS.toDouble / FileUtil.SIZE)
//        }
//        nums.toInt
//    }
//
//    final private def logMemory(): Unit = {
//        log.info("Max Memory: " + Runtime.getRuntime.maxMemory / 1048576 + " Mb")
//        log.info("Total Memory: " + Runtime.getRuntime.totalMemory / 1048576 + " Mb")
//        log.info("Free Memory: " + Runtime.getRuntime.freeMemory / 1048576 + " Mb")
//    }
//}
//
