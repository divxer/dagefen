package controllers;

import models.Album;
import org.apache.commons.lang.StringUtils;
import play.data.validation.Required;
import play.mvc.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    public static void index(int page, String searchText) {
        List<Album> albumList;
        flash.put("searchText", searchText);
        if (StringUtils.isEmpty(searchText)) {
            albumList = Album.find(
                    "order by id desc"
            ).fetch(page == 0 ? 1 : page, 24);
        } else {
            albumList = Album.find("title like ? or description like ?",
                    "%" + searchText + "%", "%" + searchText + "%").fetch(page == 0 ? 1 : page, 24);
        }

        render(albumList);
    }

    public static void postComment(
            Long albumId,
            @Required(message = "Author is required") String author,
            @Required(message = "A message is required") String content) {
        Album album = Album.findById(albumId);
        notFoundIfNull(album);
        album.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Map<String, String> result = new HashMap<String, String>();
        result.put("result", "true");
        renderJSON(result);
    }
}