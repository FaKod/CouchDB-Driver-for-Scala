package org.couchdb4s

import transfer._
import xml.XML
import com.sun.jersey.api.client.config.DefaultClientConfig
import javax.ws.rs.core.{MediaType, UriBuilder}
import scala.collection.mutable.Stack
import org.codehaus.jettison.json.{JSONArray, JSONObject}
import com.sun.jersey.api.client.{WebResource, Client}
import com.sun.jersey.api.client.filter.LoggingFilter

/**
 * 
 */
object CouchDBAccess {
  private var s: Option[CouchDBAccess] = None

  def apply() = s match {
    case Some(x) => x
    case None => s = Option(new CouchDBAccess) ; s.get
  }

}

/**
 *
 */
object Configuration {
  private var settingsFilePath = "settings.xml"

  def settingsFile_=(path:String) = settingsFilePath = path
  def settingsFile = settingsFilePath
}

/**
 *
 */
trait IdGenerator {
  self: CouchDBAccess =>

  private val idStack = new Stack[String]()

  private def fill() = {
    val o:JSONObject = baseResource.path("_uuids").queryParam("count","100").accept(MediaType.APPLICATION_JSON).get(classOf[JSONObject])
    val t = o.get("uuids").asInstanceOf[JSONArray]
    for(i <- 0 to (t.length-1) )
      idStack push t.getString(i)
  }

  def getId:String = {
    if(idStack isEmpty) fill
    idStack pop
  }
}

/**
 *
 */
class CouchDBAccess extends IdGenerator {
  private val settings = XML.loadFile(Configuration settingsFile)
  private val entry = (settings \\ "couchdb") \\ "server"
  private val port = entry \\ "@port"
  private val host = entry \\ "@host"
  private val db = entry \\ "@db"

  private val dbUri = UriBuilder.fromUri("http://" + host + ":" + port + "/" + db + "/").build()
  private val baseUri = UriBuilder.fromUri("http://" + host + ":" + port + "/").build()

  private val cc = new DefaultClientConfig
  cc.getClasses.add(classOf[JSONContextResolver])
  private val client = Client create cc
  //client.addFilter(new LoggingFilter());
  
  def resource = client resource dbUri
  def baseResource = client resource baseUri
}

abstract class ServiceBase {
  def dba = CouchDBAccess()
  
  protected def MT = MediaType.APPLICATION_JSON

  protected def doRest[A](path:String)(f:(WebResource#Builder) => A):A = {
    f(dba.resource.path(path).accept(MT).`type`(MT)).asInstanceOf[A]
  }
}

/**
 * 
 */
trait ServiceCRUD {
  self:Service =>

  def create(o:T):T = {
    o._id = dba.getId
    update(o)
  }

  def create(id:String, o:T):T = {
    o._id = id
    update(o)
  }

  def update(o:T):T =
    doRest(o._id) {
      x => o._rev = x.put(classOf[UpdateResponse], o).rev
      o
    }

  def delete(o:T):Unit =
    doRest(o._id) {
      x =>  val dr = x.header("If-Match", o._rev).delete(classOf[DeleteResponse])
      o
    }

  def read(id:String):T =
    doRest(id) {
      x => x.get(clazz)
    }
}


/**
 *
 */
trait AttachmentService {
  self:Service =>

  def createAttachment() = {

  }

  def readAttachment() = {

  }

  def deleteAttachment() = {

  }
}

/**
 *
 */
abstract class Service extends ServiceBase with ServiceCRUD {

  /*protected*/ type T <: Root
  /*protected*/ var clazz:Class[T] = null
}