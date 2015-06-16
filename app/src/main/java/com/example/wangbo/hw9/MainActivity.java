package com.example.wangbo.hw9;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import com.facebook.FacebookSdk;
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    EditText keywordText,priceFromText,priceToText;
    Button clearbutton,searchbutton;
    TextView errortext;
    Spinner spinner;
    String SetServerString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        spinner= (Spinner) findViewById(R.id.sortOrder);
        keywordText = (EditText) findViewById(R.id.keywordText);
        priceFromText = (EditText) findViewById(R.id.priceFromText);
        priceToText = (EditText) findViewById(R.id.priceToText);
        clearbutton = (Button) findViewById(R.id.clearbutton);
        searchbutton = (Button) findViewById(R.id.searchbutton);
        errortext = (TextView) findViewById(R.id.errortext);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.planets_array,android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        validate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText=(TextView)view;
        Toast.makeText(this,myText.getText(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean notempty(EditText textfield) {
        String text = textfield.getText().toString().trim();
        if (text.length() == 0) {
            return false;
        }
        return true;
    }
    public boolean largerthan(EditText lowprice,EditText highprice){
        String lowamount = lowprice.getText().toString().trim();
        String highamount = highprice.getText().toString().trim();
        if(highamount.length() == 0){
            return true;
        }else if(lowamount.length() == 0){
            return (Float.parseFloat(highamount) > 0);
        }else{
            return (Float.parseFloat(highamount) > Float.parseFloat(lowamount));
        }
    }

    private void validate() {
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!notempty(keywordText)){
                    errortext.setText("Please Enter a keyword");
                    errortext.setVisibility(View.VISIBLE);
                    return;
                }
                else if(!largerthan(priceFromText,priceToText)){
                    errortext.setText("Price To should be larger than Price From");
                    errortext.setVisibility(View.VISIBLE);
                    return;
                }
                else{
                    errortext.setVisibility(View.INVISIBLE);
                    new JSONAsyncTask().execute();
                }
            }
        });
        clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordText.setText("");
                priceFromText.setText("");
                priceToText.setText("");
                spinner.setSelection(0);
                errortext.setVisibility(View.INVISIBLE);
            }
        });
    }

    private class JSONAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls)  {
            HttpClient httpClient = new DefaultHttpClient();
            String keywordValue    = null;
            try {
                keywordValue = URLEncoder.encode(keywordText.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String URL = "http://571hw8test1env-env.elasticbeanstalk.com/index.php?Keywords="+keywordValue;
            String pricefromValue  = null;
            try {
                pricefromValue = URLEncoder.encode(priceFromText.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String pricetoValue   = null;
            try {
                pricetoValue = URLEncoder.encode(priceToText.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            URL+="&MinPrice="+pricefromValue;
            URL+="&MaxPrice="+pricetoValue;
            String sortValue = spinner.getSelectedItem().toString();
            if(sortValue.equals("Best Match")) {
                sortValue = "BestMatch";
            }else if(sortValue.equals("Price: highest first"))
                sortValue = "CurrentPriceHighest";
            else if(sortValue.equals("Price + Shipping: highest first"))
                sortValue = "PricePlusShippingHighest";
            else if(sortValue.equals("Price + Shipping: lowest first"))
                sortValue = "PricePlusShippingLowest";
            URL+="&sortOrder="+sortValue;
            URL+="&entriesPerPage=5";

            HttpGet httpget = new HttpGet(URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                SetServerString = httpClient.execute(httpget, responseHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return SetServerString;
        }
        protected void onPostExecute(String result) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(result);
                JSONObject root = new JSONObject(jsonObj.getString("json"));
                String judge = root.getString("ack");
                if( judge.equals("No results found")) {
                    errortext.setText("No Results Found");
                    errortext.setVisibility(View.VISIBLE);
                }else {
                    Intent myintent = new Intent("com.example.wangbo.hw9.DisplayActivity");
                    myintent.putExtra("jsonlearning", result);
                    startActivity(myintent);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
