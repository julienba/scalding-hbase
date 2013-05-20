package jobs

import com.twitter.scalding._
import com.twitter.scalding.Conversions._

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes

/**
sqoop import --hbase-create-table  --hbase-table widgets --column-family wi2dgetCF --hbase-row-key id --connect jdbc:mysql://localhost/hadoopguide --table widgets -m 1        
 * 
 */
class VisitCount(args: Args) extends Job(args) {
  
  // RESULT expected: <version>   column=data:nbVersion value=<nbVersion>
  val input = new HBaseSource("widgets", "localhost", 'rowid, Array("wi2dgetCF", "wi2dgetCF"), Array('version, 'widget_name))
  val output = new HBaseSource("widgetsstat", "localhost", 'version, Array("data"), Array('nbVersion))
  
  // ~ TSV --------------------------------------------------------------------
//  input.read
//    //.project('nbVersion, 'version, 'widget_name)
//    .groupBy('version){ _.size }// (length, version)
//    //.map('version -> 'version) {x : ImmutableBytesWritable => ibwToString(x) }
//    .map('size -> 'bSize) {x:Long => new ImmutableBytesWritable(Bytes.toBytes(x))}
//    .project('version, 'bSize)
//    .write(Tsv("output"))
  
  // ~ HTABLE --------------------------------------------------------------------
  input.read
    .groupBy('version){ _.size }// (length, version)
    .map('size -> 'nbVersion) {x:Long => longToibw(x)} // /!\ looks like ImmutableBytesWritable is required by com.twitter.maple.hbaseHBaseScheme
    .write(output)      
}
