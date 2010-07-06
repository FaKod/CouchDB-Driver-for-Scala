package org.couchdb4s.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.sun.jersey.api.client.UniformInterfaceException
import codecamp.couchdb._
import services.{StatusService, LocationService, UnitService, DesignViewService}
import transfer.{Root, CUnit}

trait ServiceCRUD2 {

  //def creates(o:Root)(implicit s:UnitService):Root = s.create(o)
}

class AccessTest extends FlatSpec with ShouldMatchers {

  Configuration.settingsFile = "/Users/christopherschmidt/Documents/Development/IdeaWS/codecamp1/settings.xml"
  val s = new UnitService with LocationService with StatusService with DesignViewService

  "A Test" should "start empy" in {
    try {
      val tmp = s.read("090acc8dacaaa6e82bff90e3da820325")
      s.delete(tmp)
    }
    catch {
      case _ =>
    }
  }

  "A service" should "provide 201 IDs" in {
    for(i <- 1 to 201)
      println(s.dba.getId)
  }

  "A Unit" should "be created with a gived ID" in {
    val u = new CUnit
    s.create("090acc8dacaaa6e82bff90e3da820325", u)
  }

  "A Service" should "throw exception if same ID" in {
    intercept[UniformInterfaceException] {
      val u = new CUnit
      s.create("090acc8dacaaa6e82bff90e3da820325", u)
    }
  }

  "A Service" should "create and delete 51 times" in {
    for (id <- 1 to 51) {
      var c = new CUnit
      c.unitIdentification = s.dba.getId
      c = s create c
      s delete c
    }
  }

  "A Unit" should "be able to be updated" in {
    var c: CUnit = null
    for (id <- 1 to 50) {
      c = s.read("090acc8dacaaa6e82bff90e3da820325")
      c.unitIdentification = id.toString
      s update c
    }
  }
}