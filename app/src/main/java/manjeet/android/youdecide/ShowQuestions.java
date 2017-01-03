package manjeet.android.youdecide;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ShowQuestions extends AppCompatActivity {

    ArrayList<Question> AllQuestions=new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button submit;
    String name;    ////this is username of person who conducted the survey;
    String surveyNumber;
    String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_questions);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        submit=(Button)findViewById(R.id.submitData);
        submit.setVisibility(View.INVISIBLE);
        Intent intent=getIntent();
        String url=intent.getStringExtra("URL");
        name=intent.getStringExtra("name");
        surveyNumber=intent.getStringExtra("surveyNumber");
        server=intent.getStringExtra("server");
        if(surveyNumber.equals("") || surveyNumber.equals(null))
        {
            Toast.makeText(ShowQuestions.this, "Error Loading Data", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        DownloadFile downloadFile=new DownloadFile();
        downloadFile.execute(url);
        SubmitAnswers submitListener=new SubmitAnswers();
        submitListener.userName=name;
        submitListener.surveyNumber=surveyNumber;
        submitListener.server=server;
        submitListener.context=ShowQuestions.this;
        submit.setOnClickListener(submitListener);
    }

    public class Question
    {
        public String statement;
        public ArrayList<String> options;
    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        super.onTouchEvent(event);
        return false;
    }*/
    public class DownloadFile extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            StringBuilder tempData=new StringBuilder();
            try {

                URL url = new URL(params[0]);
                //String data= URLEncoder.encode("UserName","UTF-8")+"="+URLEncoder.encode(name,"UTF-8");
                //data+="&"+URLEncoder.encode("SurveyNumber","UTF-8")+"="+URLEncoder.encode(surveyNumber,"UTF-8");
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                /*
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("GET");
                */
                connection.setConnectTimeout(10000);
               // InputStream inputStream=connection.getInputStream();
                /*
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                Log.i("Data",url.toString()+" "+data);
                wr.write(data);
                wr.flush();
                wr.close();*/
                //InputStream inputStream=getBaseContext().getResources().openRawResource(R.raw.survey_question);
                InputStream inputStream=connection.getInputStream();
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

                String line;
                while((line=bufferedReader.readLine())!=null)
                {
                    tempData.append(line);
                }
                inputStream.close();
                connection.disconnect();
            }catch (Exception e)
            {
                Log.e("FileDownloadingError", e.toString());
                Toast.makeText(ShowQuestions.this, "Can't download data", Toast.LENGTH_SHORT).show();
                ShowQuestions.this.finish();
                return null;
            }
            return tempData.toString();
        }

        @Override
        protected void onPostExecute(String data)
        {

            try {
                JSONArray mainArray = new JSONArray(data);
                for (int i = 0; i < mainArray.length(); i++) {
                    JSONObject questionObject=mainArray.getJSONObject(i);
                    String statement=questionObject.getString("statement");
                    ArrayList<String> options=new ArrayList<>();
                    JSONArray optionsArray=questionObject.getJSONArray("options");
                    for(int j=0;j<optionsArray.length();j++)
                    {
                        options.add(optionsArray.getString(j));
                    }
                    Question question=new Question();
                    question.statement=statement;
                    question.options=options;
                    AllQuestions.add(question);
                }
            }catch (Exception e)
            {
                Log.e("Data",data);
                Log.e("ErrorJson",e.toString());
                return;
            }

            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
            adapter.resetValues(AllQuestions.size());
            adapter customAdapter=new adapter(ShowQuestions.this,AllQuestions);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowQuestions.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(customAdapter);
        }
    }
}
