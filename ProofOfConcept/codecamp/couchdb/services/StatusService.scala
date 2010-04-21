package codecamp.couchdb.services

import codecamp.couchdb.Service

/**
 * Created by IntelliJ IDEA.
 * User: christopherschmidt
 * Date: 23.11.2009
 * Time: 19:21:07
 * To change this template use File | Settings | File Templates.
 */

trait StatusService {
  self:Service =>

  def status_=() = {}
  def status() = {}
}