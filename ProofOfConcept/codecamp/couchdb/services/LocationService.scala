package codecamp.couchdb.services

import codecamp.couchdb.Service

/**
 * Created by IntelliJ IDEA.
 * User: christopherschmidt
 * Date: 23.11.2009
 * Time: 19:20:19
 * To change this template use File | Settings | File Templates.
 */

trait LocationService {
  self:Service =>

  def location_=() = {}
  def location() = {}
}
