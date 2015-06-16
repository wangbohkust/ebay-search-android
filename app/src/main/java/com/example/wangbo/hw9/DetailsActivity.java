package com.example.wangbo.hw9;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


public class DetailsActivity extends Activity {
    ImageView badget;
    ImageButton buyButton;
    ImageButton facebookbutton;
    Button basicinfo;
    Button seller;
    Button shipping;
    ImageView toprated;
    ImageView expedited;
    ImageView onedayshipping;
    ImageView returnaccepted;
    RelativeLayout tab1;
    RelativeLayout tab2;
    RelativeLayout tab3;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setContentView(R.layout.activity_details);
        shareDialog.registerCallback(callbackManager,new FacebookCallback<Sharer.Result>(){

                    @Override
                    public void onSuccess(Sharer.Result result) {
                        String postid=result.getPostId();
                        if(postid==null){
                            Toast.makeText(getApplicationContext(),"Post Cancelled",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Posted Story, ID:"+postid,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(),"Post Cancelled",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException e) {

                    }
                });
        Bundle extras = getIntent().getExtras();
        String itemstring = extras.getString("item");
        final JSONObject basicInfo;
        final JSONObject sellerInfo;
        final JSONObject shippingInfo;
        try {
            JSONObject item = new JSONObject(itemstring);
            basicInfo = new JSONObject(item.getString("basicInfo"));
            sellerInfo = new JSONObject(item.getString("sellerInfo"));
            shippingInfo = new JSONObject(item.getString("shippingInfo"));
            if(basicInfo.has("pictureURLSuperSize") && !basicInfo.getString("pictureURLSuperSize").equals("")) {
                new ImageDownload((ImageView) findViewById(R.id.headImage)).execute(basicInfo.getString("pictureURLSuperSize"));
            }else{
                new ImageDownload((ImageView) findViewById(R.id.headImage)).execute(basicInfo.getString("galleryURL"));
            }
            TextView title = (TextView) findViewById(R.id.textView5);
            if(basicInfo.has("title")) {
                title.setText(basicInfo.getString("title"));
            }else{
                title.setText("N/A");
            }
            TextView shippingprice = (TextView) findViewById(R.id.textView6);
            shippingprice.setText(shippingtext(basicInfo));
            TextView location = (TextView) findViewById(R.id.textView7);
            if(basicInfo.has("location")) {
                location.setText(basicInfo.getString("location"));
            }else{
                location.setText("N/A");
            }

            badget = (ImageView)findViewById(R.id.badget);
            badget.setImageResource(R.drawable.badget);
            if(basicInfo.has("topRatedListing")&&basicInfo.getString("topRatedListing").equals("true")) {
                badget.setVisibility(View.VISIBLE);
            }
            buyButton = (ImageButton) findViewById(R.id.buyButton);
            buyButton.setImageResource(R.drawable.buynow);
            buyButton.setOnClickListener(new View.OnClickListener() {
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
            facebookbutton = (ImageButton) findViewById(R.id.facebookbutton);
            facebookbutton.setImageResource(R.drawable.facebook);

            facebookbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ShareDialog.canShow(ShareLinkContent.class)) {
                        try {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentTitle(basicInfo.getString("title"))
                                    .setContentDescription(shippingtext(basicInfo) + " Location: " + basicInfo.getString("location"))
                                    .setContentUrl(Uri.parse(basicInfo.getString("viewItemURL")))
                                    .setImageUrl(Uri.parse(basicInfo.getString("galleryURL")))
                                    .build();
                            shareDialog.show(linkContent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            TextView companyname = (TextView) findViewById(R.id.textView9);
            if(basicInfo.has("categoryName")&&!basicInfo.getString("categoryName").equals("")) {
                companyname.setText(basicInfo.getString("categoryName"));
            }else{
                companyname.setText("N/A");
            }
            TextView condition = (TextView) findViewById(R.id.textView11);
            if(basicInfo.has("conditionDisplayName")&&!basicInfo.getString("conditionDisplayName").equals("")){
                condition.setText(basicInfo.getString("conditionDisplayName"));
            }else{
                condition.setText("N/A");
            }
            TextView buyingformat = (TextView) findViewById(R.id.textView13);
            if(basicInfo.has("listingType")&&!basicInfo.getString("listingType").equals("")) {
                if(basicInfo.getString("listingType").equals("FixedPrice")||basicInfo.getString("listingType").equals("StoreInventory")) {
                    buyingformat.setText("Buy It Now");
                }else if(basicInfo.getString("listingType").equals("Auction")){
                    buyingformat.setText("Auction");
                }else if(basicInfo.getString("listingType").equals("Classified")){
                    buyingformat.setText("Classified Ad");
                }
            }else{
                buyingformat.setText("N/A");
            }

            TextView username = (TextView) findViewById(R.id.textView20);
            if(sellerInfo.has("sellerUserName")&&!sellerInfo.getString("sellerUserName").equals("")) {
                username.setText(sellerInfo.getString("sellerUserName"));
            }else{
                username.setText("N/A");
            }
            TextView feedbackscore = (TextView) findViewById(R.id.textView21);
            if(sellerInfo.has("feedbackScore")&&!sellerInfo.getString("feedbackScore").equals("")) {
                feedbackscore.setText(sellerInfo.getString("feedbackScore"));
            }else{
                feedbackscore.setText("N/A");
            }
            TextView positivefeedback = (TextView) findViewById(R.id.textView22);
            if(sellerInfo.has("positiveFeedbackPercent")&&!sellerInfo.getString("positiveFeedbackPercent").equals("")) {
                positivefeedback.setText(sellerInfo.getString("positiveFeedbackPercent"));
            }else{
                positivefeedback.setText("N/A");
            }
            TextView feedbackrating = (TextView) findViewById(R.id.textView23);
            if(sellerInfo.has("feedbackRatingStar")&&!sellerInfo.getString("feedbackRatingStar").equals("")) {
                feedbackrating.setText(sellerInfo.getString("feedbackRatingStar"));
            }else{
                feedbackrating.setText("N/A");
            }
            toprated = (ImageView) findViewById(R.id.textView24);
            if(sellerInfo.has("topRatedSeller")&&sellerInfo.getString("topRatedSeller").equals("true")) {
                toprated.setImageResource(R.drawable.correct);
            }else{
                toprated.setImageResource(R.drawable.incorrect);
            }
            TextView store = (TextView) findViewById(R.id.textView25);
            if(sellerInfo.has("sellerStoreName")&&!sellerInfo.getString("sellerStoreName").equals("")) {
                store.setText(sellerInfo.getString("sellerStoreName"));
            }else{
                store.setText("N/A");
            }
            TextView shippingtype = (TextView) findViewById(R.id.textView32);
            if(shippingInfo.has("shippingType")&&!shippingInfo.getString("shippingType").equals("")) {
                String str=shippingInfo.getString("shippingType");
                String res = "";
                for(int i = 0; i < str.length(); i++) {
                    Character ch = str.charAt(i);
                    if(Character.isUpperCase(ch))
                        res += " " + ch;
                    else
                        res += ch;
                }
                shippingtype.setText(res);
            }else{
                shippingtype.setText("N/A");
            }
            TextView handlingtime = (TextView) findViewById(R.id.textView33);
            if(shippingInfo.has("handlingTime")&&!shippingInfo.getString("handlingTime").equals("")) {
                handlingtime.setText(shippingInfo.getString("handlingTime")+" day(s)");
            }else{
                handlingtime.setText("N/A");
            }
            TextView shippinglocation = (TextView) findViewById(R.id.textView34);
            if(shippingInfo.has("shipToLocations")&&!shippingInfo.getString("shipToLocations").equals("")) {
                shippinglocation.setText(shippingInfo.getString("shipToLocations"));
            }else{
                shippinglocation.setText("N/A");
            }
            expedited = (ImageView) findViewById(R.id.textView35);
            if(shippingInfo.has("expeditedShipping")&&shippingInfo.getString("expeditedShipping").equals("true")) {
                expedited.setImageResource(R.drawable.correct);
            }else{
                expedited.setImageResource(R.drawable.incorrect);
            }
            onedayshipping = (ImageView) findViewById(R.id.textView36);
            if(shippingInfo.has("oneDayShippingAvailable")&&shippingInfo.getString("oneDayShippingAvailable").equals("true")) {
                onedayshipping.setImageResource(R.drawable.correct);
            }else{
                onedayshipping.setImageResource(R.drawable.incorrect);
            }
            returnaccepted = (ImageView) findViewById(R.id.textView37);
            if(shippingInfo.has("returnsAccepted")&&shippingInfo.getString("returnsAccepted").equals("true")) {
                returnaccepted.setImageResource(R.drawable.correct);
            }else{
                returnaccepted.setImageResource(R.drawable.incorrect);
            }

            basicinfo = (Button) findViewById(R.id.imageButton);
            basicinfo.setBackgroundResource(R.drawable.button_pressed);
            seller = (Button) findViewById(R.id.imageButton2);
            seller.setBackgroundResource(R.drawable.default_button);
            shipping = (Button) findViewById(R.id.imageButton3);
            shipping.setBackgroundResource(R.drawable.default_button);
            tab1 = (RelativeLayout) findViewById(R.id.tab1);
            tab2 = (RelativeLayout) findViewById(R.id.tab2);
            tab3 = (RelativeLayout) findViewById(R.id.tab3);
            basicinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    basicinfo.setBackgroundResource(R.drawable.button_pressed);
                    seller.setBackgroundResource(R.drawable.default_button);
                    shipping.setBackgroundResource(R.drawable.default_button);
                    tab1.setVisibility(View.VISIBLE);
                    tab2.setVisibility(View.INVISIBLE);
                    tab3.setVisibility(View.INVISIBLE);
                }
            });
            seller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    seller.setBackgroundResource(R.drawable.button_pressed);
                    basicinfo.setBackgroundResource(R.drawable.default_button);
                    shipping.setBackgroundResource(R.drawable.default_button);
                    tab2.setVisibility(View.VISIBLE);
                    tab1.setVisibility(View.INVISIBLE);
                    tab3.setVisibility(View.INVISIBLE);
                }
            });
            shipping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    shipping.setBackgroundResource(R.drawable.button_pressed);
                    basicinfo.setBackgroundResource(R.drawable.default_button);
                    seller.setBackgroundResource(R.drawable.default_button);
                    tab3.setVisibility(View.VISIBLE);
                    tab2.setVisibility(View.INVISIBLE);
                    tab1.setVisibility(View.INVISIBLE);
                }
            });

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
        getMenuInflater().inflate(R.menu.menu_details, menu);
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
