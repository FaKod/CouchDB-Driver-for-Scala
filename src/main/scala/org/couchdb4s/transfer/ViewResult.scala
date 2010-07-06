package org.couchdb4s.transfer

import collection.mutable.HashMap


class ViewResult(total_rows:Int, offset:Int) {
  private val r = HashMap[String, (Any,Any)]()
  def rows = r


  override def toString = "rows: " + total_rows + " offset:" + offset + " size: " + rows.size
}

object ViewResult {
  def apply(total_rows:Int, offset:Int) = new ViewResult(total_rows, offset)
}