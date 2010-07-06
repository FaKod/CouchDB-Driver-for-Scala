package org.couchdb4s.test

import org.specs.SpecificationWithJUnit
import org.couchdb4s.services.{UnitService, DesignViewService}
import org.couchdb4s.transfer.{View, DesignView}
import org.couchdb4s.Configuration

class DesignDocumentTest extends SpecificationWithJUnit {
  Configuration.settingsFile = "/Users/christopherschmidt/Documents/Development/IdeaWS/codecamp1/settings.xml"
  val s = new UnitService with DesignViewService

  def startEmpty = {
    try {
      val company = s.readDesignView("company")
      s.deleteDesignView(company)
    }
    catch {
      case _ =>
    }
  }

  "A DesignView" should {
    "method build convert in both directions" in {
      val view = DesignView()
      view._id = "company"
      view += View("all",
        "function(doc) { if (doc.Type == 'customer')  emit(null, doc) }")
      view += View("by_lastname",
        "function(doc) { if (doc.Type == 'customer')  emit(doc.LastName, doc) }")
      view += View("total_purchases",
        "function(doc) { if (doc.Type == 'purchase')  emit(doc.Customer, doc.Amount) }",
        "function(keys, values) { return sum(values) }")

      val json = s.build(view)
      val vvv = s.build(json)

      view._id must_== vvv._id
      view._rev must_== vvv._rev
      view.views must_== vvv.views
    }

    "A Service insert and read a design view" in {
      val view = DesignView()
      view._id = "company"
      view += View("all",
        "function(doc) { if (doc.Type == 'customer')  emit(null, doc) }")
      view += View("by_lastname",
        "function(doc) { if (doc.Type == 'customer')  emit(doc.LastName, doc) }")
      view += View("total_purchases",
        "function(doc) { if (doc.Type == 'purchase')  emit(doc.Customer, doc.Amount) }",
        "function(keys, values) { return sum(values) }")

      s.createDesignView(view)
      val company = s.readDesignView("company")

      view._id must_== company._id
      view._rev must_== company._rev
      view.views must_== company.views
    }
  }
}