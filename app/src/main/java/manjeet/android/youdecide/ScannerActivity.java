package manjeet.android.youdecide;

import android.content.Intent;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.util.jar.Manifest;

public class ScannerActivity extends AppCompatActivity {

    QRCodeReaderView qrCodeReaderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        qrCodeReaderView=(QRCodeReaderView)findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setOnQRCodeReadListener(new qrCodeListener());
    }
    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
    public class qrCodeListener implements QRCodeReaderView.OnQRCodeReadListener
    {
        @Override
        public void onQRCodeRead(String text, PointF[] points) {
            //Toast.makeText(ScannerActivity.this, text, Toast.LENGTH_SHORT).show();
            //format of URL is http://mkumar123.byethost9.com/users/mkumar/survey1/surveyQuestion.json
            qrCodeReaderView.stopCamera();
            String[] tempArray=text.split("/");
            Log.i("url",text);
            Log.i("tempArray",tempArray[0] + "-" + tempArray[1] + "-" + tempArray[2]+"-"+tempArray[3]+"-"+tempArray[4]);
            String name=tempArray[4];
            String surveyNumber=tempArray[5].substring(6);
            String server=tempArray[0]+"/"+tempArray[1]+"/"+tempArray[2]+"/";
            Intent intent=new Intent(ScannerActivity.this,ShowQuestions.class);
            intent.putExtra("URL",text);
            intent.putExtra("name",name);
            intent.putExtra("surveyNumber",surveyNumber);
            intent.putExtra("server",server);
            startActivity(intent);
        }
    }

}
