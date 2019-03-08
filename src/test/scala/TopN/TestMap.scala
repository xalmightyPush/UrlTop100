package TopN

import java.{lang, util}

import TopN.Utils.OpenHashMap

import scala.collection.JavaConversions._
import org.mapdb.{DB, DBMaker, HTreeMap, Serializer}
import java.util.Comparator

import scala.util.Random

object TestMap {
    def main(args: Array[String]): Unit = {
        //        val map = new OpenHashMap[String, Long]()
        //        for (i <- 1 to 10) {
        //            val key = "1"
        //            val count: Long = if (map.contains(key)) {
        //                map.apply(key) + 1
        //            } else {
        //                1
        //            }
        //            map.update(key, count)
        //        }
        //        map.foreach(println(_))

        val db: DB = DBMaker.fileDB("D:\\soucecode\\data\\myDB")
            .fileMmapEnable()
            .fileMmapEnableIfSupported()
            .fileMmapPreclearDisable()
            .allocateIncrement(1024 * 1024 * 1024)
            .cleanerHackEnable()
            .make()

        val map: HTreeMap[java.lang.String, java.lang.Long] = db.hashMap("name_of_map")
            .keySerializer(Serializer.STRING)
            .valueSerializer(Serializer.LONG)
            .create()

        for (i <- 1 to 1000000) {
            val rand = new Random()
            val key = rand.nextInt(10000)
            val count: Long = if (map.containsKey(key.toString)) {
                map.apply(key.toString) + 1
            } else {
                1
            }
            map.update(key.toString, count)
        }

        map.foreach(println(_))

        val treemap = new util.TreeMap[java.lang.Long, java.lang.String](new Comparator[lang.Long] {
            override def compare(y: lang.Long, x: lang.Long): Int = return if (x < y) -1 else 1
        })

        map.foreach(element => {
            treemap.put(element._2, element._1)
            if (treemap.size > 100) { //只保留前面TopN个元素
                treemap.pollLastEntry
            }
        })

        treemap.foreach(e => println(e._2 + " " + e._1))
    }
}
