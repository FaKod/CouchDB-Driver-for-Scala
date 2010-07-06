package org.couchdb4s.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import codecamp.couchdb.services.{DesignViewService, UnitService}
import codecamp.couchdb.Configuration
import codecamp.couchdb.transfer.{View, DesignView, CUnit}
import collection.mutable.Stack

class UnitHierarchyTest extends FlatSpec with ShouldMatchers {

  Configuration.settingsFile = "/Users/christopherschmidt/Documents/Development/IdeaWS/codecamp1/settings.xml"
  val s = new UnitService with DesignViewService

  def createSuccessors(u:CUnit, stack:Stack[CUnit], length:Int, depth:Int):Unit = {
    stack push u
    if (depth != 0) {
      for(i <- 1 to length) {
        var u1 = new CUnit()
        u1.commandedBy = u._id
        u1.unitIdentification = "I: " + i + " D: " + depth
        u1 = s create u1
        createSuccessors(u1, stack, length, depth - 1)
      }
    }
  }

  "The Test" should "provide a specific hierarchy" in {
    var master = new CUnit()
    master.commandedBy = ""
    master.unitIdentification = "Master"
    master = s create master

    val stack = new Stack[CUnit]
    createSuccessors(master, stack, 3, 3)

    stack.foreach(s delete(_))
  }

  "A View" should "return the right successors" in {
    try {
      val successorsOld = s.readDesignView("successors")
      s.deleteDesignView(successorsOld)
    }
    catch {
      case _ =>
    }

    var master = new CUnit()
    master.commandedBy = ""
    master.unitIdentification = "Master"
    master = s create master

    val stack = new Stack[CUnit]
    createSuccessors(master, stack, 4, 4)

    var view = DesignView()
    view._id = "successors"
    view += View("successorsView", "function(doc) { if (doc.Type == '" + classOf[CUnit].getSimpleName + "')  emit(doc.commandedBy, doc._id) }")
    view = s createDesignView view

    stack.foreach { x =>
      val result = s doDesignView(view, "successorsView", x _id )
      val su = for(t <- result rows ; (_, (_, successor)) = t) yield successor
      println("CUnit with id " + x._id + " commands: " + ( if(su.size>0) su else "noone") )
    }
    stack.foreach(s delete(_))

    val successorsOld = s readDesignView("successors")
    s deleteDesignView(successorsOld)
  }
}