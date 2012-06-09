package controllers;

import models.Album;
import models.Picture;
import play.mvc.Controller;
import utils.UpYunUtils;

/**
 * User: divxer
 * Date: 12-6-8
 * Time: 下午11:11
 */
public class Albums extends Controller {
    public static void show(Long id) {
        Album album = Album.findById(id);
        for (Picture picture : album.pictures) {
            picture.fileName = UpYunUtils.picBedDomain + "/"
                    + UpYunUtils.getTinyUrl(album.id.toString()) + "/" + picture.fileName;
        }
        render(album);
    }
}
