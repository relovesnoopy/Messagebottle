package jp.ac.hal.messagebottle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class toImageActivity extends AppCompatActivity {

    //private Bitmap bitmapimage = null;
    private boolean isfocus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_image);

        //Intentの受け取り
        String strbitmap = (String)getIntent().getSerializableExtra("image");
        ImageView iv = (ImageView)findViewById(R.id.imagedetail);
        if(strbitmap != null){
            //画像適正サイズに変換後セット
            iv.setImageBitmap(new ImageManage().scaleBitmap(strbitmap));

        }else {
            Toast.makeText(this,"画像の取得に失敗しました",Toast.LENGTH_SHORT).show();
            finish();
        }


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }








}
