package org.couchdb4s.test

import com.sun.jersey.api.client.UniformInterfaceException
import org.couchdb4s.transfer.CUnit
import org.couchdb4s.services.{DesignViewService, StatusService, LocationService, UnitService}
import org.couchdb4s.Configuration
import org.specs.SpecificationWithJUnit

trait ServiceCRUD2 {

  //def creates(o:Root)(implicit s:UnitService):Root = s.create(o)
}

class AccessTest extends SpecificationWithJUnit {
  Configuration.settingsFile = "/Users/christopherschmidt/Documents/Development/IdeaWS/codecamp1/settings.xml"
  val s = new UnitService with LocationService with StatusService with DesignViewService

  def startEmpty = {
    try {
      val tmp = s.read("090acc8dacaaa6e82bff90e3da820325")
      s.delete(tmp)
    }
    catch {
      case _ =>
    }
  }

  "A service" should { startEmpty.before
    "provide 201 IDs" in {
      for (i <- 1 to 201)
        println(s.dba.getId)
    }

    "A Unit be created with a gived ID" in {
      val u = new CUnit
      s.create("090acc8dacaaa6e82bff90e3da820325", u)
    }

    "throw exception if same ID" in {
      {
        val u = new CUnit
        s.create("090acc8dacaaa6e82bff90e3da820325", u)
      } must throwA[UniformInterfaceException]
    }

    "create and delete 51 times" in {
      for (id <- 1 to 51) {
        var c = new CUnit
        c.unitIdentification = s.dba.getId
        c = s create c
        s delete c
      }
    }

    "A Unit be able to be updated" in {
      var c: CUnit = null
      for (id <- 1 to 50) {
        c = s.read("090acc8dacaaa6e82bff90e3da820325")
        c.unitIdentification = id.toString
        s update c
      }
    }
  }
}