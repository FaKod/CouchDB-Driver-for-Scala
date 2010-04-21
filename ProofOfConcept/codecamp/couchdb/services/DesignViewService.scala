package codecamp.couchdb.services

import codecamp.couchdb.Service
import codecamp.couchdb.transfer._
import org.codehaus.jettison.json.{JSONArray, JSONObject}

/**
 *
 */
trait DesignViewService {
  self:Service =>
  type A = DesignView

  final def designPath = "_design/"
  final def viewPath = "/_view/"

  def doDesignView(o:A, viewName:String, value:String):ViewResult =
    doDesignView(o._id, viewName, value)
  
  def doDesignView(id:String, viewName:String, value:String):ViewResult = {
    val jo:JSONObject = dba.resource.path(designPath + id + viewPath + viewName).
            queryParam("key", "\"" + value + "\"").accept(MT).get(classOf[JSONObject])

    val viewResult = ViewResult(jo.getInt("total_rows"), jo.getInt("offset"))
    val jsonArray = jo.getJSONArray("rows")
      for( t <- 0 to jsonArray.length-1) {
        val j = jsonArray.getJSONObject(t)
        viewResult.rows += ((j.getString("id"), (j.get("key"), j.get("value"))))
      }
    viewResult
  }

  def createDesignView(o:A):A = {
    o._rev = null
    updateDesignView(o)
  }

  def updateDesignView(o:A):A =
    doRest(designPath + o._id) {
      x => o._rev = x.put(classOf[UpdateResponse], build(o)).rev
      o
    }

  def deleteDesignView(o:A):Unit =
    doRest(designPath + o._id) {
      x =>  val dr = x.header("If-Match", o._rev).delete(classOf[DeleteResponse])
      o
    }

  def readDesignView(id:String):A =
    doRest(designPath + id) {
      x => build( x.get(classOf[JSONObject]) )
    }

  def build(v:DesignView):JSONObject = {
    val view = new JSONObject()
    v.views.foreach {
      x =>
      val tmp = new JSONObject().put("map", x.map).put("reduce", x.reduce)
      view.put(x.name, tmp)
    }

    new JSONObject(v, Array("_id", "_rev")).put("language", v.language).put("views", view)
  }

  def build(j:JSONObject):DesignView = {
    val dv = DesignView()

    dv._id = j.getString("_id").replaceFirst(designPath,"")
    if(!j.isNull("_rev"))
      dv._rev = j.getString("_rev")
    dv.language = j.getString("language")

    val viewsList = j.get("views").asInstanceOf[JSONObject]
    val viewsIter = viewsList.keys
    while(viewsIter hasNext) {
      val viewName = viewsIter.next.asInstanceOf[String]
      val v = viewsList.getJSONObject(viewName)
      dv.views += View(viewName,v.getString("map"),
        if(v.isNull("reduce"))
          null
        else
          v.getString("reduce")
      )
    }
    dv
  }
}
