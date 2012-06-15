package controllers;

import play.mvc.With;

/**
 * User: divxer
 * Date: 12-6-15
 * Time: 下午10:09
 */
@With(Secure.class)
public class Users extends CRUD {
}
