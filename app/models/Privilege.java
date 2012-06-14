package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * 权限
 * User: divxer
 * Date: 12-6-12
 * Time: 上午12:48
 */
@Entity
public class Privilege extends Model {
    public String name;
    public String type;
}
