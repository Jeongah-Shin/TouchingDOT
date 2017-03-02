package com.dotincorpkr.englidot.Alphabet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.JsonThread;
import com.dotincorpkr.englidot.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.dotincorpkr.englidot.Alphabet.AlphabetTestMultipleActivity.wrongAnswer;
import static com.dotincorpkr.englidot.Alphabet.AlphabetTestMultipleActivity.wrongAnswer_length;

/**
 * Created by wjddk on 2017-02-09.
 */

public class MultipleTestResultActivity extends BaseActivity {
    int testScore = 10-wrongAnswer_length;
    TextView result;
    TextView wrongABC;
    Button gotoReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_test_result);
        //테스트 결과 wrongAnswer 가 없다면 다시
        result= (TextView)findViewById(R.id.result);
        wrongABC=(TextView)findViewById(R.id.wrongABC);
        gotoReview=(Button)findViewById(R.id.gotoReview);

        result.setText(testScore+"/10");
        wrongABC.setText(wrongAnswer.toString());

        gotoReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlphabetReviewListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        insertToDatabase("M",Integer.toString(testScore), wrongAnswer.toString());

        Thread reviewThr = new JsonThread("testResultJson.php");
        reviewThr.start();
    }


    private void insertToDatabase(String db_testArea, String db_testScore, String db_wrongAnswer){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MultipleTestResultActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String db_testArea = (String)params[0];
                    String db_testScore = (String) params[1];
                    String db_wrongAnswer = (String)params[2];

                    String data  = URLEncoder.encode("testArea", "UTF-8") + "=" + URLEncoder.encode(db_testArea, "UTF-8")
                            +"&" + URLEncoder.encode("testScore", "UTF-8") + "=" + URLEncoder.encode(db_testScore, "UTF-8")
                            + "&" + URLEncoder.encode("wrongAnswer", "UTF-8") + "=" + URLEncoder.encode(db_wrongAnswer, "UTF-8");
                    byte[] postData       = data.getBytes( StandardCharsets.UTF_8 );
                    int    postDataLength = postData.length;
                    String request = "http://englidot.azurewebsites.net/data_insert.php";
                    URL url = new URL(request);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                    conn.setDoOutput( true );
                    conn.setInstanceFollowRedirects( false );
                    conn.setRequestMethod( "POST" );
                    conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty( "charset", "utf-8");
                    conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                    conn.setUseCaches( false );
                    try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                        wr.write( postData );
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = reader.readLine();

                    // Read Server Response
                    while(line != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(db_testArea,db_testScore,db_wrongAnswer);
        //task.execute("M",Integer.toString(testScore), wrongAnswer.toString());
    }

}


