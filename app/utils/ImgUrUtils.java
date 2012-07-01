package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.ImgUrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import play.Logger;
import play.server.hybi10.Base64;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * User: divxer
 * Date: 12-7-1
 * Time: 上午11:09
 */
public class ImgurUtils {
    /**
     * 上传图片至imgur
     * @param imgUrl 上传图片的源地址（URL）
     * @return imgur上的存储地址（URL）
     */
    public static String uploadImgs2Imgur(String imgUrl) {
        String PROTECTED_RESOURCE_URL = "http://api.imgur.com/2/account/images.json";

        // Replace these with your own api key and secret (you'll need an read/write api key)
        String apiKey = "38b23387b8461b9f06365428cbf4af8e04fefd0d4";
        String apiSecret = "37502a32ac1ada3de4fe5921a87773af";
        OAuthService service = new ServiceBuilder().provider(ImgUrApi.class).apiKey(apiKey).apiSecret(apiSecret).build();

        // Trade the Request Token and Verfier for the Access Token
        Token accessToken = new Token("70bf766280ff295993db5b302083e90904fefd118", "66d9b2b53f9305bf644bbc598169d17a");

        // Now let's go and ask for a protected resource!
        OAuthRequest request = new OAuthRequest(Verb.POST, PROTECTED_RESOURCE_URL);
        request.addQuerystringParameter("key", apiKey);
        request.addQuerystringParameter("image", imgUrl);
        request.addQuerystringParameter("type", "url");
        service.signRequest(accessToken, request);
        Response response = request.send();

        String originalLink = null;
        if (response.getCode() == 200) {
            try{
                com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
                JsonElement json = jsonParser.parse(response.getBody());
                JsonObject jsonObject = json.getAsJsonObject();

                if (jsonObject != null) {
                    if (jsonObject.get("images") != null) {
                        originalLink = jsonObject.get("images")
                                .getAsJsonObject().get("links").getAsJsonObject().get("original").getAsString();
                    } else if (jsonObject.get("error") != null) {
                        String errorMessage = jsonObject.get("error").getAsJsonObject().get("message").getAsString();
                        Logger.error("error in upload image to imgur. " + errorMessage);
                    } else {
                        Logger.error("error in upload image to imgur.");
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception ex) {
                Logger.error(ex, "error in upload image to imgur.");
                return null;
            }
        } else {
            Logger.error("upload img status: " + response.getCode());
        }

        return originalLink;
    }
}
