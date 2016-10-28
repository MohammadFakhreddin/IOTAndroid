package com.example.espadan.fileuploader2;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button fileChooserBtn;
    private TextView fileUrl;
    private Button fileUploaderBtn;
    private String HOST_URL = "http://192.168.1.116:1820";
    //private final String UPLOAD_URL = /*"http://192.168.1.2:3000*/"/download/upload";
    // private final String BUTTONS_URL = /*"http://192.168.1.2:3000*/"/button/orders";
    private final String UPLOAD = "/upload";
    private final String NEXT = "/next";
    private final String PREV = "/back";
    private final String CLOSE = "/quit";
    private final String OPEN = "/open";
    private final int BROWSE_FILE = 125;
    private String selectedUrl = "";
    private final String APP_TAG = "fileUploader";
    private Button onBtn;
    private Button offBtn;
    private Button previousBtn;
    private Button nextBtn;
    private EditText ipNumEditText;
    private Button ipSetBtn;
    private Button fileNameBtn;
    private EditText setNameEdit;
    private final String fileBase = "/";//"/home/pi/Documents/I1820-RPi/AoLab-Presenter/slides/";
    private String fileName = "intter.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileChooserBtn = (Button) findViewById(R.id.fileChooserBtn);
        fileUrl = (TextView) findViewById(R.id.fileUrl);
        fileUploaderBtn = (Button) findViewById(R.id.uploaderBtn);
        onBtn = (Button) findViewById(R.id.onBtn);
        offBtn = (Button) findViewById(R.id.offBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        previousBtn = (Button) findViewById(R.id.previousBtn);
        ipNumEditText = (EditText) findViewById(R.id.ipNum);
        ipSetBtn = (Button) findViewById(R.id.setIp);
        setNameEdit = (EditText) findViewById(R.id.fileName);
        fileNameBtn = (Button) findViewById(R.id.setNameBtn);
        fileChooserBtn.setOnClickListener(this);
        fileUploaderBtn.setOnClickListener(this);
        onBtn.setOnClickListener(this);
        offBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        fileNameBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        ipSetBtn.setOnClickListener(this);
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
    public void onClick(View v) {
        if (v == fileChooserBtn) {
            Uri fileUrl = Uri.parse(Environment.getExternalStorageDirectory() + "");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(fileUrl, "resource/folder");
            if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                startActivityForResult(intent, BROWSE_FILE);
            } else {
                Toast.makeText(this, "File manager not installed", Toast.LENGTH_SHORT).show();
            }
        } else if (v == fileUploaderBtn) {
            if (!selectedUrl.equals("")) {
                try {
                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            String fileUrl = (String) params[0];
                            try {
                                File file = new File(new URI(fileUrl));
                                HttpClient httpclient = new DefaultHttpClient();
                                URI uri = new URI(HOST_URL + UPLOAD);
                                HttpPost httppost = new HttpPost(uri);
                                //InputStreamEntity reqEntity = null;
                                //reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
                                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                                multipartEntity.addPart("file", new FileBody(file));
                                httppost.setEntity(multipartEntity);
                                try {
                                    HttpResponse response = httpclient.execute(httppost);
                                    Log.i(APP_TAG, "Response is :" + response.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }

                            //Do something with response...
                            return null;
                        }
                    };
                    task.execute(selectedUrl);
                } catch (Exception e) {
                    // show error
                }
            }
        } else if (v == onBtn) {
            try {
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            URI uri = new URI(HOST_URL + "" + OPEN + fileBase + fileName);
                            HttpGet httpGet = new HttpGet(uri);
                            //List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                            //nameValuePair.add(new BasicNameValuePair("command", "1"));
                            //httpGet.setEntity(new UrlEncodedFormEntity(nameValuePair));
                            try {
                                HttpResponse response = httpclient.execute(httpGet);
                                Log.i(APP_TAG, "Response is :" + response.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        //Do something with response...
                        return null;
                    }
                };
                task.execute(selectedUrl);
            } catch (Exception e) {
                // show error
            }
        } else if (v == offBtn) {
            try {
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            URI uri = new URI(HOST_URL + CLOSE);
                            HttpGet httpGet = new HttpGet(uri);
                            //List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                            // nameValuePair.add(new BasicNameValuePair("command", "2"));
                            //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                            try {
                                HttpResponse response = httpclient.execute(httpGet);
                                Log.i(APP_TAG, "Response is :" + response.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        //Do something with response...
                        return null;
                    }
                };
                task.execute(selectedUrl);
            } catch (Exception e) {
                // show error
            }
        } else if (v == nextBtn) {
            try {
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            URI uri = new URI(HOST_URL + NEXT);
                            HttpGet httpGet = new HttpGet(uri);
                            // List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                            // nameValuePair.add(new BasicNameValuePair("command", "3"));
                            //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                            try {
                                HttpResponse response = httpclient.execute(httpGet);
                                Log.i(APP_TAG, "Response is :" + response.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                task.execute(selectedUrl);
            } catch (Exception e) {
                // show error
            }
        } else if (v == previousBtn) {
            try {
                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            URI uri = new URI(HOST_URL + PREV);
                            HttpGet httpGet = new HttpGet(uri);
                            //Dictionary<String, String> nameAndValue = new Hashtable<>();
//                            parameters : command:@Enum:{1:on , 2:off , 3:next , 4:previus}
//                            response : {status:true}
                            //Encoding POST data
                            //List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                            //nameValuePair.add(new BasicNameValuePair("command", "4"));
                            //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                            try {
                                HttpResponse response = httpclient.execute(httpGet);
                                Log.i(APP_TAG, "Response is :" + response.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        //Do something with response...
                        return null;
                    }
                };
                task.execute(selectedUrl);
            } catch (Exception e) {
                // show error
            }
        } else if (v == ipSetBtn) {
            if (!ipNumEditText.getText().toString().replace(" ", "").equals("")) {
                HOST_URL = "http://" + ipNumEditText.getText().toString().replace(" ", "") + ":1820";
                Toast.makeText(MainActivity.this, "Ip is set " + HOST_URL + "", Toast.LENGTH_LONG).show();
            }
        } else if (v == fileNameBtn) {
            if (!setNameEdit.getText().toString().replace(" ", "").equals("")) {
                fileName = setNameEdit.getText().toString().replace(" ", "");
                Toast.makeText(MainActivity.this, "Name is set " + fileBase + "" + fileName, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BROWSE_FILE) {
            if (resultCode == RESULT_OK) {
                selectedUrl = data.getDataString();
                //selectedUrl = selectedUrl.split("emulated")[1];

                fileUrl.setText(selectedUrl + "");
            }
        }
    }
}
