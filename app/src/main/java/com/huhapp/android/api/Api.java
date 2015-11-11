package com.huhapp.android.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huhapp.android.api.model.Comment;
import com.huhapp.android.api.model.Notification;
import com.huhapp.android.api.model.Page;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.api.model.QuestionType;
import com.huhapp.android.api.model.Setting;
import com.huhapp.android.api.model.User;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.exception.UnauthorizedException;
import com.huhapp.android.util.PropertyAccessor;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 4/11/15.
 */
public class Api {
//    public static final String ENDPOINT = "https://staging-huh-app.herokuapp.com/";
    public static final String ENDPOINT = "https://huh-app.herokuapp.com/";
    public static final String ENDPOINT_USER_LOGIN = "api/user/login";
    public static final String ENDPOINT_USER_LOGIN_CHECK = "api/user/login/check";
    public static final String ENDPOINT_USER_CREATE = "api/user/create";
    public static final String ENDPOINT_USER_ADD_LOCATION = "api/user/location";
    public static final String ENDPOINT_USER_ADD_GCM_TOKEN = "api/user/addgcmtoken";
    public static final String ENDPOINT_SETTING_LIST = "api/setting/list";
    public static final String ENDPOINT_SETTING_UPDATE = "api/setting/update";
    public static final String ENDPOINT_NOTIFICATION_LIST = "api/notification/list";
    public static final String ENDPOINT_NOTIFICATION_MARK_ALL_AS_READ = "api/notification/markallasread";
    public static final String ENDPOINT_VOTE_UP = "api/vote/up";
    public static final String ENDPOINT_QUESTION_TYPE_LIST = "api/questiontype/list";
    public static final String ENDPOINT_VOTE_DOWN = "api/vote/down";
    public static final String ENDPOINT_QUESTION_CREATE = "api/question/create";
    public static final String ENDPOINT_QUESTION_VIEW = "api/question/view";
    public static final String ENDPOINT_QUESTION_FLAG = "api/question/flag";
    public static final String ENDPOINT_QUESTIONS_LATEST = "api/question/recent";
    public static final String ENDPOINT_QUESTIONS_TRENDING = "api/question/trending";
    public static final String ENDPOINT_QUESTIONS_NEAR = "api/question/near";
    public static final String ENDPOINT_QUESTIONS_MINE = "api/question/mine";
    public static final String ENDPOINT_QUESTIONS_COMMENTED = "api/question/commented";
    public static final String ENDPOINT_QUESTIONS_FAVORITES = "api/question/favorites";
    public static final String ENDPOINT_COMMENTS_LIST = "api/comment/list";
    public static final String ENDPOINT_COMMENTS_CREATE = "api/comment/create";
    public static final String ENDPOINT_COMMENT_FLAG = "api/comment/flag";


    public static final String ENDPOINT_PAGE = "api/page/view";


    public static final int RESPONSE_CODE_OK = 200;
    public static final int RESPONSE_CODE_UNAUTHORIZED = 401;

    public static final String JSON_TAG_CODE = "code";
    public static final String JSON_TAG_MESSAGE = "message";
    public static final String JSON_TAG_RESPONSE = "response";

    public static String getImageUrl(Question question) {
        return ENDPOINT + "images/usericons/" + question.getUsername().toLowerCase() + ".png";
    }
    public static String getImageUrl(Comment comment) {
        return ENDPOINT + "images/usericons/" + comment.getUsername().toLowerCase() + ".png";
    }

    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    //NO NEED FOR DATE DESERIALIZER
                /*SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null))
                             .addDeserializer(Date.class, new JsonDateDeserializer());
                         mapper.registerModule(testModule);*/


    public static HttpResponse makeRequest(String path, Map<String, String> params) throws Exception {

        //url with the post data
        HttpPost httpPost = new HttpPost(path);

        //convert parameters into JSON object

        StringBuffer content = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            content.append(entry.getKey());
            content.append("=");
            content.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            content.append("&");
        }
        //remove last &
        if (content.length() > 0) {
            content.setLength(content.length() - 1);
        }

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(content.toString());

        //sets the post request as the resulting string
        httpPost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

        //Handles what is returned from the page
        //ResponseHandler responseHandler = new BasicResponseHandler();

        //instantiates httpclient to make request
        DefaultHttpClient httpClient = new DefaultHttpClient();
        return httpClient.execute(httpPost);
    }

    private static JSONObject makeRequestParsed(String path, Map<String, String> params) {
        return makeRequestParsedAux(path, params, 0);
    }

    private static JSONObject makeRequestParsedAux(String path, Map<String, String> params, int tries) {
        try {
            if (PropertyAccessor.getToken() != null) {
                params.put("token", PropertyAccessor.getToken());
            }
            HttpResponse httpResponse = makeRequest(path, params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode == RESPONSE_CODE_OK) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString("UTF-8");

                //Log.e("API", output);
                return new JSONObject(output);
            } else if (responseCode == RESPONSE_CODE_UNAUTHORIZED && tries < 2){
                Log.i("API", "CALL UNAUTH, Trying to login/create user. Try: " + tries);
                userAuth();
                if (PropertyAccessor.getToken() != null) {
                    params.put("token", PropertyAccessor.getToken());
                }
                return makeRequestParsedAux(path, params, tries + 1);
            } else {
                Log.i("API", "CALL ERROR. Try: " + tries);
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e("API", "MakeRequestParsedAux: " + path + " " +e.getMessage(), e);
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }

    private static void userAuth() {
        String username = PropertyAccessor.getUsername();
        String password = PropertyAccessor.getPassword();
        User user = null;
        if (username != null && username.length() > 0) {
            user = login(username, password);
        }

        if (user != null) {
            Log.i("API", "User logged in");
            PropertyAccessor.setToken(user.getToken());
            return;
        }

        user = createUser();
        if (user == null) {
            Log.i("API", "Couldn't create user");
            return;
        }

        PropertyAccessor.setUsername(user.getUsername());
        PropertyAccessor.setPassword(user.getPassword());

        user = login(user.getUsername(), user.getPassword());

        Log.i("API", "User created and logged in");

        PropertyAccessor.setToken(user.getToken());
    }

    private static <T> T makeRequestParsedForObject(String path, Map<String, String> params, Class<T> type) {
        try {
            JSONObject jsonObj = Api.makeRequestParsed(path, params);
            if (jsonObj == null || jsonObj.isNull(JSON_TAG_RESPONSE)) {
                return null;
            } else if (type.equals(String.class)) {
                return (T) jsonObj.getString(JSON_TAG_RESPONSE);
            } else {
                JavaType jType = mapper.getTypeFactory().constructType(type);
                return mapper.readValue(
                        String.valueOf(jsonObj.getJSONObject(JSON_TAG_RESPONSE))
                        , jType);
            }
        } catch (Exception e) {
            Log.e("API", "MakeRequestParsedForObject: " + path + " " + e.getMessage(), e);
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }

    private static <E> List<E> makeRequestParsedForList(String path, Map<String, String> params, Class<E> type) {
        try {
            JSONObject jsonObj = Api.makeRequestParsed(path, params);
            JavaType jType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            return mapper.readValue(
                    String.valueOf(jsonObj.getJSONArray(JSON_TAG_RESPONSE)), jType);
        } catch (Exception e) {
            Log.e("API", e.getMessage(), e);
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }

    ///
    public static User createUser() {
        Map<String, String> params = new HashMap<String, String>();
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_USER_CREATE, params, User.class);
    }

    public static User login(String username, String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_USER_LOGIN, params, User.class);
    }

    public static String testToken() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",  PropertyAccessor.getToken());
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_USER_LOGIN_CHECK, params, String.class);
    }

    public static Boolean addLocation(double longitude, double latitude) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("longitude", longitude + "");
        params.put("latitude", latitude  + "");
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_USER_ADD_LOCATION, params, Boolean.class);
    }

    public static String addGCMToken(String gcmToken) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("gcmtoken", gcmToken);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_USER_ADD_GCM_TOKEN, params, String.class);
    }

    public static List<Question> getQuestionBySomething(String endpoint, double longitude, double latitude) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("longitude", longitude + "");
        params.put("latitude", latitude  + "");
        return Api.makeRequestParsedForList(ENDPOINT + endpoint, params, Question.class);
    }

    public static Question questionVoteUp(String questionId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionId", questionId);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_VOTE_UP, params, Question.class);
    }

    public static Question questionVoteDown(String questionId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionId", questionId);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_VOTE_DOWN, params, Question.class);
    }

    public static Question getQuestion(String questionId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionId", questionId);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_QUESTION_VIEW, params, Question.class);
    }

    public static List<Comment> getComments(String questionId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionId", questionId);
        return Api.makeRequestParsedForList(ENDPOINT + ENDPOINT_COMMENTS_LIST, params, Comment.class);
    }

    public static Comment commentVoteUp(String commentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("commentId", commentId);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_VOTE_UP, params, Comment.class);
    }

    public static Comment commentVoteDown(String commentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("commentId", commentId);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_VOTE_DOWN, params, Comment.class);
    }

    public static Comment createComment(String text, String questionId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionId", questionId);
        params.put("text", text);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_COMMENTS_CREATE, params, Comment.class);
    }
    public static List<Notification> notificationList() {
        Map<String, String> params = new HashMap<String, String>();
        return Api.makeRequestParsedForList(ENDPOINT + ENDPOINT_NOTIFICATION_LIST, params, Notification.class);
    }
    public static Boolean notificationMarkAllAsRead() {
        Map<String, String> params = new HashMap<String, String>();
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_NOTIFICATION_MARK_ALL_AS_READ, params, Boolean.class);
    }

    public static List<QuestionType> questionTypeList() {
        Map<String, String> params = new HashMap<String, String>();
        return Api.makeRequestParsedForList(ENDPOINT + ENDPOINT_QUESTION_TYPE_LIST, params, QuestionType.class);
    }

    public static List<Setting> settingList() {
        Map<String, String> params = new HashMap<String, String>();
        return Api.makeRequestParsedForList(ENDPOINT + ENDPOINT_SETTING_LIST, params, Setting.class);
    }
    public static Boolean updateSetting(String name, Boolean value) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("value", value.toString());
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_SETTING_UPDATE, params, Boolean.class);
    }

    public static Question questionCreate(String questionType, String text, double longitude, double latitude) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("type", questionType);
        params.put("text", text);
        params.put("longitude", longitude + "");
        params.put("latitude", latitude  + "");

        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_QUESTION_CREATE, params, Question.class);
    }

    public static Void questionFlag(String questionId, String reason) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("questionId", questionId);
        params.put("reason", reason);

        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_QUESTION_FLAG, params, Void.class);
    }

    public static Void commentFlag(String commentId, String reason) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("commentId", commentId);
        params.put("reason", reason);

        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_COMMENT_FLAG, params, Void.class);
    }

    public static Page pageView(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", url);
        return Api.makeRequestParsedForObject(ENDPOINT + ENDPOINT_PAGE, params, Page.class);
    }

}

