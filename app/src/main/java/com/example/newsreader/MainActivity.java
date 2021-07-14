package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ArrayList<NewsItem> news;

    private RecyclerView recyclerView;
    private TextView txtTitle;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initData();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        new GetNews().execute();


    }


    private void initData(){
        Log.d(TAG, "initData: started");
        news=new ArrayList<>();
        adapter=new NewsAdapter(this);
        recyclerView=findViewById(R.id.recyclerView);
    }
    private class GetNews extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream=getInputStream();
            if (null!=inputStream) {
                try {
                    initXMLPullParser(inputStream);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter.setNews(news);
        }

        private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {

            Log.d(TAG, "initXMLPullParser: started");

            XmlPullParser parser= Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(inputStream, null);

            parser.next();


            parser.require(XmlPullParser.START_TAG, null, "rss");


            while (parser.next() != XmlPullParser.END_TAG) {


                if(parser.getEventType() != XmlPullParser.START_TAG){
                    continue;
                }


                parser.require(XmlPullParser.START_TAG, null, "channel");


                while(parser.next()!=XmlPullParser.END_TAG){

                    if(parser.getEventType() != XmlPullParser.START_TAG){
                        continue;
                    }

                    if(parser.getName().equals("item")){
                        parser.require(XmlPullParser.START_TAG, null, "item");

                        String title="", description="", link="", date="";

                        while(parser.next()!= XmlPullParser.END_TAG){
                            if(parser.getEventType() != XmlPullParser.START_TAG){
                                continue;
                            }
                            String tagName=parser.getName();
                            if(tagName.equals("title")){
                                title=getContent(parser, "title");
                            }else if(tagName.equals("description")){
                                description=getContent(parser, "description");
                            }else if(tagName.equals("link")){
                                link=getContent(parser, "link");
                            }else if(tagName.equals("pubdate")){
                                date=getContent(parser, "pubdate");
                            }else{
                                skipTag(parser);
                            }
                        }
                        NewsItem item=new NewsItem(title, description, link, date);
                        news.add(item);

                    }else{
                        skipTag(parser);
                    }
                }
            }
        }
        private String getContent(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
            String content="";
            parser.require(XmlPullParser.START_TAG, null, tagName);

            if(parser.next() == XmlPullParser.TEXT){
                content= parser.getText();
                parser.next();
            }
            return content;
        }
        private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
            if(parser.getEventType()!=XmlPullParser.START_TAG){
                throw new IllegalStateException();
            }
            int num=1;
            while(num!=0){
                switch(parser.next()){
                    case XmlPullParser.START_TAG:
                        num++;
                        break;
                    case XmlPullParser.END_TAG:
                        num--;
                        break;
                    default:
                        break;
                }
            }
        }
        private InputStream getInputStream(){
            try {
                URL url=new URL("https://www.moneycontrol.com/rss/iponews.xml");
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                return connection.getInputStream();
            }catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}