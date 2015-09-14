package com.huhapp.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import com.huhapp.android.api.Api;
import com.huhapp.android.api.model.Page;

public class WebSettingsActivity extends Activity {

    private String url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_settings);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        Bundle b = getIntent().getExtras();
        this.url = b.getString("url");
        this.title = b.getString("title");

        new GetPage().execute();
        setTitle(this.title);
    }


    private class GetPage extends AsyncTask<Void,Void,Page> {

        @Override
        protected Page doInBackground(Void... voids) {
            return Api.pageView(url);
        }

        @Override
        protected void onPostExecute(Page result) {
            super.onPostExecute(result);

            if (result != null) {
                WebView webView = (WebView) findViewById(R.id.webView);
                webView.loadDataWithBaseURL(url, result.getHtml(), "text/html", "UTF-8", url);
            }
        }
    }

}
