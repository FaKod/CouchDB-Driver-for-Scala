package org.couchdb4s.transfer

import collection.mutable.{ListBuffer}


case class View(name:String, map:String, reduce:String = null)


class DesignView extends Root {
  var language = "javascript"
  private val v = new ListBuffer[View]()

  def +=(v:View) = this.v += v
  def views = v
}

object DesignView {
  def apply() = new DesignView()
}