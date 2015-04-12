package com.huhapp.android.api;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.huhapp.android.api.model.Question;
import com.huhapp.android.common.logger.Log;
import com.huhapp.android.util.JsonDateDeserializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 4/11/15.
 */
public class Api {
    public static final String ENDPOINT = "https://huh-app.herokuapp.com/";
    public static final String ENDPOINT_QUESTIONS_LATEST = "api/question/recent";
    public static final String ENDPOINT_QUESTIONS_TRENDING = "api/question/trending";
    public static final String ENDPOINT_QUESTIONS_NEAR = "api/question/near";
    public static final String ENDPOINT_QUESTIONS_MINE = "api/question/mine";
    public static final String ENDPOINT_QUESTIONS_COMMENTED = "api/question/commented";
    public static final String ENDPOINT_QUESTIONS_FAVORITES = "api/question/favorites";


    public static final int RESPONSE_CODE_OK = 200;

    public static final String JSON_TAG_CODE = "code";
    public static final String JSON_TAG_MESSAGE = "message";
    public static final String JSON_TAG_RESPONSE = "response";

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


    public static List<Question> getQuestionBySomething(String endpoint) {
        endpoint = ENDPOINT + endpoint;
        try {
            HttpResponse httpResponse = makeRequest(endpoint, new HashMap<String, String>());
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode == RESPONSE_CODE_OK) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString("UTF-8");
                JSONObject jsonObj = new JSONObject(output);

                List<Question> questions = mapper.readValue(
                        String.valueOf(jsonObj.getJSONArray(JSON_TAG_RESPONSE))
                        ,  new TypeReference<List<Question>>(){});

                return questions;

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e("API", e.getMessage());
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }


    public static List<Question> getQuestionByLatest() {
        return getQuestionBySomething(ENDPOINT + ENDPOINT_QUESTIONS_LATEST);
    }
}
