package com.example.layoutforminip;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    int[] valueArray=new int[10];

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;
    ImageView imageView10;

    Menu mymenu;

    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1=(ImageView) findViewById(R.id.car1);
        imageView2=(ImageView) findViewById(R.id.car2);
        imageView3=(ImageView) findViewById(R.id.car3);
        imageView4=(ImageView) findViewById(R.id.car4);
        imageView5=(ImageView) findViewById(R.id.car5);
        imageView6=(ImageView) findViewById(R.id.car6);
        imageView7=(ImageView) findViewById(R.id.car7);
        imageView8=(ImageView) findViewById(R.id.car8);
        imageView9=(ImageView) findViewById(R.id.car9);
        imageView10=(ImageView) findViewById(R.id.car10);

        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        imageView4.setVisibility(View.INVISIBLE);
        imageView5.setVisibility(View.INVISIBLE);
        imageView6.setVisibility(View.INVISIBLE);
        imageView7.setVisibility(View.INVISIBLE);
        imageView8.setVisibility(View.INVISIBLE);
        imageView9.setVisibility(View.INVISIBLE);
        imageView10.setVisibility(View.INVISIBLE);

        textView=(TextView) findViewById(R.id.lotNumberAvailable);


        new GetData(MainActivity.this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        mymenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh){
            LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ImageView refresh=(ImageView)inflater.inflate(R.layout.menu_refresher,null);
            //ImageView refresh=(ImageView)item.getActionView();
            Animation rotation= AnimationUtils.loadAnimation(this,R.anim.rotate_refresher);
            rotation.setRepeatCount(Animation.INFINITE);
            refresh.startAnimation(rotation);
            item.setActionView(refresh);


            new GetData(MainActivity.this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetData extends AsyncTask<Void, Void, Void> {
        private Context context;
        private byte count=10;
        private GetData(Context c){
            context=c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Retrieving data...", Toast.LENGTH_LONG).show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpGetHandler sh = new HttpGetHandler();
            // Making a request to url and getting response
            String url = "https://dweet.io/get/latest/dweet/for/jimmykueh123";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray withObj=jsonObj.getJSONArray("with");
                    JSONObject conObj=withObj.getJSONObject(0);
                    JSONObject contentObj=conObj.getJSONObject("content");

                    // Getting JSON Array node
                    JSONArray array = contentObj.getJSONArray("array");

                    for (int i=0;i<array.length();i++){
                        JSONObject c=array.getJSONObject(i);
                        valueArray[i]=c.getInt("value");

                        if(valueArray[i]==1){
                            count--;
                        }
                    }


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get data from Dweet.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get data from Dweet.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ((MainActivity)context).stopUpdating();

            setImageVisibility(valueArray[0],imageView1);
            setImageVisibility(valueArray[1],imageView2);
            setImageVisibility(valueArray[2],imageView3);
            setImageVisibility(valueArray[3],imageView4);
            setImageVisibility(valueArray[4],imageView5);
            setImageVisibility(valueArray[5],imageView6);
            setImageVisibility(valueArray[6],imageView7);
            setImageVisibility(valueArray[7],imageView8);
            setImageVisibility(valueArray[8],imageView9);
            setImageVisibility(valueArray[9],imageView10);


            if(count>6){
                textView.setTextColor(Color.GREEN);
                textView.setText(count+" lots remain.");
            }
            else if(count>3 && count<=6){
                textView.setTextColor(Color.rgb(240,180,0));// orange
                textView.setText(count+" lots remain.");
            }
            else if (count>1 && count <=3){
                textView.setTextColor(Color.RED);
                textView.setText(count+" lots only.");
            }
            else if (count==1){
                textView.setTextColor(Color.RED);
                textView.setText(count+" lot only!!");
            }
            else{
                textView.setTextColor(Color.RED);
                textView.setText("PARKING FULL!");
            }


        }
    }

    private void setImageVisibility(int v, ImageView lv){
        if(v==1){
            lv.setVisibility(View.VISIBLE);
        }
        else if (v==0){
            lv.setVisibility(View.INVISIBLE);
        }
    }

    public void stopUpdating(){
        try {
            MenuItem item = mymenu.findItem(R.id.action_refresh);
            if (item.getActionView() != null) {
                item.getActionView().clearAnimation();
                item.setActionView(null);
            }
        }catch(NullPointerException e){
            Toast.makeText(MainActivity.this,"Null",Toast.LENGTH_SHORT).show();
        }
    }


}
