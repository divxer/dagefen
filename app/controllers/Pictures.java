package controllers;

import models.Picture;
import play.mvc.Controller;
import utils.UpYunUtils;

/**
 * User: divxer
 * Date: 12-6-8
 * Time: 下午11:30
 */
public class Pictures extends Controller {
    public static void show(Long id) {
        Picture picture = Picture.findById(id);
        picture.fileName = UpYunUtils.picBedDomain + "/"
                + UpYunUtils.getTinyUrl(picture.album.id.toString()) + "/" + picture.fileName;
        render(picture);
    }
}
