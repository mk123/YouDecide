package manjeet.android.youdecide;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    Button button;

    int RequestCode=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int CameraCheck=ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
                if(CameraCheck==PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, ScannerActivity.class));
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.CAMERA},RequestCode);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int resultCode,String permissions[],int grantResults[])
    {
       if(resultCode==RequestCode)
       {
           if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED )
           {
               startActivity(new Intent(MainActivity.this, ScannerActivity.class));
           }
       }

    }
}
