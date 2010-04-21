package codecamp.couchdb.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import codecamp.couchdb.transfer.{View, DesignView}
import codecamp.couchdb.{Configuration}
import codecamp.couchdb.services.{UnitService, DesignViewService}

class DesignDocumentTest extends FlatSpec with ShouldMatchers {

  Configuration.settingsFile = "/Users/christopherschmidt/Documents/Development/IdeaWS/codecamp1/settings.xml"
  val s = new UnitService with DesignViewService

  "A Test" should "start empy" in {
    try {
      val company = s.readDesignView("company")
      s.deleteDesignView(company)
    }
    catch {
      case _ =>
    }
  }

  "method build" should "convert in both directions" in {
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

    view._id should equal (vvv._id)
    view._rev should equal (vvv._rev)
    view.views.equals(vvv.views) should be (true)
  }

  "A Service" should "insert and read a design view" in {
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

    view._id should equal (company._id)
    view._rev should equal (company._rev)
    view.views.equals(company.views) should be (true)
  }
}