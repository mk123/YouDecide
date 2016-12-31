package manjeet.android.youdecide;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by manjeet on 10/22/16.
 */
public class SubmitAnswers implements View.OnClickListener {
    String userName;
    String surveyNumber;
    String server;
    ShowQuestions context;  ///for activity context;
    String answers;
    ///format in which answer would be posted is:
    // 3,4,1,2  representing 3rd option in 1st question, 4th option in 2nd question and so on.
    @Override
    public void onClick(View v) {
        if(adapter.questionsAnswered!=adapter.totalQuestions)
        {
            Toast.makeText(context, "Please answer all question.", Toast.LENGTH_LONG).show();
            //Toast.makeText(context,adapter.questionsAnswered+" "+adapter.totalQuestions,Toast.LENGTH_LONG).show();
            return;
        }
        server+="setAnswers.php";

        answers=Integer.toString(adapter.selectedOption[0]);
        ///saving answers
        for (int i = 1; i < adapter.selectedOption.length; i++) {
            answers+=","+Integer.toString(adapter.selectedOption[i]);
        }
        PostAnswers task=new PostAnswers();
        task.execute(userName,surveyNumber,server,answers);

    }

    public class PostAnswers extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            context.recyclerView.setVisibility(View.INVISIBLE);
            context.submit.setVisibility(View.INVISIBLE);
            context.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[2]);


                String data= URLEncoder.encode("UserName","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8");
                data+="&"+URLEncoder.encode("SurveyNumber","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
                data+="&"+URLEncoder.encode("Answers","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8");
                Log.e("doInBackground",data);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(6000);
                connection.setRequestMethod("POST");

                OutputStreamWriter writer=new OutputStreamWriter(connection.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();
                Log.e("doInBackground","outputStream closed");
                InputStream inputStream=connection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result=new StringBuilder();
                String tempString;
                while((tempString=reader.readLine())!=null)
                {
                    result.append(tempString);
                }
                inputStream.close();
                connection.disconnect();
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            context.progressBar.setVisibility(View.INVISIBLE);
            Log.e("doInBackground","Result "+result);
            if(result!=null)
            {
                Toast.makeText(context,"Thank You",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Error Submitting Answers", Toast.LENGTH_SHORT).show();
            }
            Intent intent=new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}
