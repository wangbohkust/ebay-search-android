package com.example.wangbo.hw9;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class DisplayActivity extends Activity {

    TextView searchhead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Bundle extras = getIntent().getExtras();
        String jsonstring = extras.getString("jsonlearning");
        try {
            JSONObject jsonObj = new JSONObject(jsonstring);
            searchhead = (TextView) findViewById(R.id.searchname);
            searchhead.setText("Results for '"+jsonObj.getString("Keywords")+"'");
            JSONObject root = new JSONObject(jsonObj.getString("json"));
            TableLayout outtablelayout=(TableLayout) findViewById(R.id.outtablelayout);
                int count = root.getInt("resultCount");
                for(int i=0;i<count;i++) {
                    final JSONObject item = new JSONObject(root.getString("item"+i));
                    final JSONObject basicInfo = new JSONObject(item.getString("basicInfo"));
                    View line = (View) outtablelayout.getChildAt(2*i);
                    line.setVisibility(View.VISIBLE);
                    TableRow row = (TableRow) outtablelayout.getChildAt(2*i+1);
                    TableLayout imagelayout = (TableLayout) row.getChildAt(0);
                    TableRow imagerow = (TableRow) imagelayout.getChildAt(0);
                    if(basicInfo.has("galleryURL")) {
                        new ImageDownload((ImageView) imagerow.getChildAt(0)).execute(basicInfo.getString("galleryURL"));
                    }
                    imagerow.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if(basicInfo.has("viewItemURL")) {
                                String viewItemURL = null;
                                try {
                                    viewItemURL = basicInfo.getString("viewItemURL");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewItemURL));
                                startActivity(intent);
                            }
                        }
                    });
                    TableLayout textlayout = (TableLayout) row.getChildAt(1);
                    TableRow textrow1 = (TableRow) textlayout.getChildAt(0);
                    TableRow textrow2 = (TableRow) textlayout.getChildAt(1);
                    TextView title = (TextView) textrow1.getChildAt(0);
                    if(basicInfo.has("title")) {
                        title.setText(basicInfo.getString("title"));
                    }else{
                        title.setText("N/A");
                    }
                    final String itemString=item.toString();
                    title.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View arg0) {
                            Intent myintent=new Intent("com.example.wangbo.hw9.DetailsActivity");
                            myintent.putExtra("item", itemString);
                            startActivity(myintent);
                        }
                    });
                    TextView shipping = (TextView) textrow2.getChildAt(0);
                    shipping.setText(shippingtext(basicInfo));
                }
                for(int j=count;j<5;j++){
                    TableRow row = (TableRow) outtablelayout.getChildAt(2*j+1);
                    row.setVisibility(View.INVISIBLE);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String shippingtext(JSONObject basicInfo) throws JSONException {
            String shippInginfo="";
            float totalPrice=0;
            totalPrice =Float.parseFloat(basicInfo.getString("convertedCurrentPrice"));
            if(basicInfo.has("shippingServiceCost")) {
                if (basicInfo.getString("shippingServiceCost").equals("") || Float.parseFloat(basicInfo.getString("shippingServiceCost")) == 0) {
                    shippInginfo = "(FREE Shipping)";
                } else {
                    totalPrice += Float.parseFloat(basicInfo.getString("shippingServiceCost"));
                    shippInginfo = "(+$" + basicInfo.getString("shippingServiceCost") + " for Shipping)";
                }
                return "$" + String.format("%.2f", totalPrice) + " " + shippInginfo;
            }else{
                return "N/A";
            }
    }

    private class ImageDownload extends AsyncTask<String, Void, Bitmap> {
        ImageView image;

        public ImageDownload(ImageView image) {
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
