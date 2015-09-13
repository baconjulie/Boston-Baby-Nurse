package org.example.android.bostonbabynurse;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    protected String xmlData;

    protected ListView listViewArticles;
    public ArrayList<Article> allArticles = new ArrayList<>();

    private static String TAG = MainActivity.class.getSimpleName();

    protected ListView mDrawerList;
    protected RelativeLayout mDrawerPane;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;

    Context mContext;

    protected ArrayList<NavItem> mNavItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listViewArticles = (ListView) findViewById(R.id.articleList);

        mNavItems.add(new NavItem("Home", "The latest from the Boston Baby Nurse blog", R.drawable.ic_home_black_48dp));
        mNavItems.add(new NavItem("Events", "Upcoming Boston Baby Nurse events", R.drawable.ic_event_black_48dp));
        mNavItems.add(new NavItem("Community forum", "Reach out and connect with new parents", R.drawable.ic_forum_black_48dp));
        mNavItems.add(new NavItem("Education", "Learning materials for new parents", R.drawable.ic_class_black_48dp));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the navigation drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        new DownloadData().execute("http://bostonbabynurse.com/feed/");

        for (Article art : allArticles) {
            Log.d("allArticles:  ", art.toString());
        }

        ArticleAdapter articleAdapter = new ArticleAdapter(this, allArticles);

        listViewArticles.setVisibility(listViewArticles.VISIBLE);
        listViewArticles.setAdapter(articleAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            } else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText(mNavItems.get(position).mTitle);
            subtitleView.setText(mNavItems.get(position).mSubtitle);
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }


    public class ArticleAdapter extends ArrayAdapter<Article> {

        public ArticleAdapter(Context context, ArrayList<Article> articles) {
            super(context, 0, articles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.article_list_item_view, null);
            } else {
                view = convertView;
            }

            TextView title = (TextView) view.findViewById(R.id.articleTitle);
            TextView pubDate = (TextView) view.findViewById(R.id.articlePubDate);
            TextView description = (TextView) view.findViewById(R.id.articleDescription);

            //ImageView icon = (ImageView) view.findViewById(R.id.articleIcon);



            title.setText(allArticles.get(position).getTitle());
            pubDate.setText((allArticles.get(position).getPubDate()).substring(0, 16));
            description.setText(android.text.Html.fromHtml(allArticles.get(position).getDescription()).toString());
            //icon.setImageResource(allArticles.get(position).ge);

            return view;
        }
    }

    // Called when a particular item from the drawer is selected
    protected void selectItemFromDrawer(int position) {
        Intent intent;
        Log.v("MainActivity", " this is " + position);

        switch(position) {
            default:
            case 0:
                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(MainActivity.this, ForumActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(MainActivity.this, EducationActivity.class);
                startActivity(intent);
                break;
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        String myXmlData;

        protected String doInBackground(String...urls) {
            try {
                myXmlData = downloadXML(urls[0]);

            } catch (IOException e) {
                return "Unable to download XML file.";
            }

            return "";
        }

        protected void onPostExecute(String result) {
            Log.d("onPostExecute", myXmlData);
            xmlData = myXmlData;

                Log.d("PushedBTN", "Pushed button");
                ParseArticles parse = new ParseArticles(xmlData);
                boolean operationStatus = parse.process();
                if (operationStatus) {
                    allArticles = parse.getArticles();

                    ArticleAdapter articleAdapter = new ArticleAdapter(MainActivity.this, allArticles);
                    listViewArticles.setVisibility(listViewArticles.VISIBLE);
                    listViewArticles.setAdapter(articleAdapter);

                } else {
                    Log.d("MainActivity", "Error parsing file");
                }
        }

        private String downloadXML(String theUrl) throws IOException {
            int BUFFER_SIZE = 2000;
            InputStream is = null;

            String xmlContents = "";

            try {
                URL url = new URL(theUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                int response = conn.getResponseCode();
                Log.d("DownloadXML", "The response returned is: " + response);
                is = conn.getInputStream();

                InputStreamReader isr = new InputStreamReader(is);
                int charRead;
                char[] inputBuffer = new char[BUFFER_SIZE];
                try {
                    while ((charRead = isr.read(inputBuffer)) > 0 ) {
                        String readString = String.copyValueOf(inputBuffer, 0, charRead);
                        xmlContents += readString;
                        inputBuffer = new char[BUFFER_SIZE];
                    }

                    return xmlContents;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } finally {
                if (is != null)
                    is.close();
            }
        }
    }
}
