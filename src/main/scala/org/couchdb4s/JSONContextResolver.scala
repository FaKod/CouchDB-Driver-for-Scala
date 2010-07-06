package org.couchdb4s


import javax.ws.rs.ext.ContextResolver
import javax.xml.bind.JAXBContext
import com.sun.jersey.api.json.{JSONConfiguration, JSONJAXBContext}
import javax.ws.rs.ext.Provider
import java.lang.Class
import transfer.{DeleteResponse, UpdateResponse, CUnit}

@Provider
class JSONContextResolver extends ContextResolver[JAXBContext] {
  val cTypes = Array(classOf[CUnit], classOf[UpdateResponse], classOf[DeleteResponse])

  val context = new JSONJAXBContext(JSONConfiguration.natural().build(), cTypes: _*)

  val types:Set[Class[_]] = Set(cTypes: _*)

  def getContext(objectType: Class[_]): JAXBContext = {
//    if (types.contains(objectType))
//      context
//    else
      null
  }
}