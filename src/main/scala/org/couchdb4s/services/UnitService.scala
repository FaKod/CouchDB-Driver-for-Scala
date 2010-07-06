package org.couchdb4s.services

import org.couchdb4s.Service
import org.couchdb4s.transfer.CUnit

/**
 * Created by IntelliJ IDEA.
 * User: christopherschmidt
 * Date: 23.11.2009
 * Time: 19:19:14
 * To change this template use File | Settings | File Templates.
 */

class UnitService extends Service {
  type T = CUnit
  clazz = classOf[CUnit]
}