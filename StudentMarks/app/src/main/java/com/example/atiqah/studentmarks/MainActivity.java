package com.example.atiqah.studentmarks;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText name,matric_no,marks,time,date;
    Button add,view,viewall,delete,modify;
    SQLiteDatabase db;
    String strDate,strTime;

    WebServiceCall wsc = new WebServiceCall();
    JSONObject jsnObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        matric_no = (EditText) findViewById(R.id.matric_no);
        marks = (EditText) findViewById(R.id.marks);
        time = (EditText) findViewById(R.id.time);
        date = (EditText) findViewById(R.id.date);
        add = (Button) findViewById(R.id.addbtn);
        view = (Button) findViewById(R.id.viewbtn);
        viewall = (Button) findViewById(R.id.viewallbtn);
        delete = (Button) findViewById(R.id.deletebtn);
        modify = (Button) findViewById(R.id.modifybtn);


        db = openOrCreateDatabase("Student_manage", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS student(matricno VARCHAR,name VARCHAR,marks INTEGER);");

        Runnable run = new Runnable() {
            @Override
            public void run() {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn","fnGetDateTime"));

                try {
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(),"POST",params);
                    strDate = jsnObj.getString("currDate");
                    strTime = jsnObj.getString("currTime");
                }catch(Exception e){
                    strDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    strTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time.setText(strTime);
                        date.setText(strDate);
                    }
                });
            }
        };

        Thread thrDateTime = new Thread(run);
        thrDateTime.start();
    }
        public void fnAdd(View vw){

            Runnable run = new Runnable() {
                @Override
                public void run() {

                    // TODO Auto-generated method stub

                    db.execSQL("INSERT INTO student VALUES('"+matric_no.getText()+"','"+name.getText()+
                            "','"+marks.getText()+"');");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TODO Auto-generated method stub
                            if(matric_no.getText().toString().trim().length()==0||
                                    name.getText().toString().trim().length()==0||
                                    marks.getText().toString().trim().length()==0)
                            {
                                showMessage("Error", "Please enter all values");
                                return;
                            }
                            Toast showSuccess = Toast.makeText(getApplicationContext(),"Success, Record added successfully",Toast.LENGTH_SHORT);
                            showSuccess.show();
                            clearText();

                        }
                    });
                }
            };

            Thread thrSave = new Thread(run);
            thrSave.start();

        }

    public void fnViewAll(View vw){

        Runnable run = new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                final Cursor c=db.rawQuery("SELECT * FROM student", null);


                final StringBuffer buffer=new StringBuffer();
                while(c.moveToNext()) {
                    buffer.append("Matric No: "+c.getString(0)+"\n");
                    buffer.append("Name: "+c.getString(1)+"\n");
                    buffer.append("Marks: "+c.getString(2)+"\n\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO Auto-generated method stub
                        if(c.getCount()==0)
                        {
                            showMessage("Error", "No records found");
                            return;
                        }

                        showMessage("Student Details", buffer.toString());

                    }
                });
            }
        };

        Thread thrViewAll = new Thread(run);
        thrViewAll.start();

    }

    public void fnDelete(View vw) {

        Runnable run = new Runnable() {
            @Override
            public void run() {
                final Cursor c=db.rawQuery("SELECT * FROM student WHERE matricno='"+matric_no.getText()+"'", null);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(matric_no.getText().toString().trim().length()==0)
                        {
                            showMessage("Error", "Please enter MatricNo");
                            return;
                        }
                        if(c.moveToFirst())
                        {
                            db.execSQL("DELETE FROM student WHERE matricno='"+matric_no.getText()+"'");
                            showMessage("Success", "Record Deleted");
                        }
                        else
                        {
                            showMessage("Error", "Invalid MatricNo");

                        }
                        clearText();
                    }
                });
            }
        };

        Thread thrDelete = new Thread(run);
        thrDelete.start();
    }

    public void fnModify(View vw) {

        Runnable run = new Runnable() {
            @Override
            public void run() {

                final Cursor c=db.rawQuery("SELECT * FROM student WHERE matricno='"+matric_no.getText()+"'", null);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(matric_no.getText().toString().trim().length()==0)
                        {
                            showMessage("Error", "Please enter MatricNo");
                            return;
                        }
                        if(c.moveToFirst())
                        {
                            db.execSQL("UPDATE student SET name='"+name.getText()+"',marks='"+marks.getText()+
                                    "' WHERE matricno='"+matric_no.getText()+"'");
                            showMessage("Success", "Record Modified");
                        }
                        else
                        {
                            showMessage("Error", "Invalid MatricNo");

                        }
                        clearText();
                    }
                });
            }
        };

        Thread thrModify = new Thread(run);
        thrModify.start();
    }

    public void fnView(View vw) {

        Runnable run = new Runnable() {
            @Override
            public void run() {


                final Cursor c=db.rawQuery("SELECT * FROM student WHERE matricno='"+matric_no.getText()+"'", null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(matric_no.getText().toString().trim().length()==0)
                        {
                            showMessage("Error", "Please enter MatricNo");
                            return;
                        }
                        if(c.moveToFirst())
                        {
                            name.setText(c.getString(1));
                            marks.setText(c.getString(2));
                        }
                        else
                        {
                            showMessage("Error", "Invalid MatricNo");
                            clearText();
                        }
                    }
                });
            }
        };

        Thread thrView = new Thread(run);
        thrView.start();
    }


    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clearText()
    {
        matric_no.setText("");
        name.setText("");
        marks.setText("");
        matric_no.requestFocus();
    }

    public class WebServiceCall{

        JSONObject jsonObj;
        String strUrl = "";

        public WebServiceCall()
        {
            jsonObj = null;
            strUrl = "http://192.168.1.18/webServiceJSON/globalWebService.php";
        }

        public String fnGetURL()
        {
            return strUrl;
        }

        public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params)
        {
            InputStream is = null;
            String json = "";
            JSONObject jObj = null;

            try{
                if(method == "POST"){
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } else if(method == "GET")
                {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) !=null){
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
                jObj = new JSONObject(json);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            return jObj;
        }
    }


}
