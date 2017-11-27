package jp.ac.hal.messagebottle;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ImageUploadActivity extends AppCompatActivity {
    private CustomListAdapter customListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        //端末のサイズ取得

        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        Point size = new Point();
        disp.getSize(size);

        toImageActivity.view_width = size.x;
        toImageActivity.view_height = size.y;

        //Intentの受け取り
        String strbitmap = (String)getIntent().getSerializableExtra("picture");
        if(strbitmap != null){
            List<ImageEntity> imageEntityList = new ArrayList<>();
            ListView listView = (ListView)findViewById(R.id.list_id);

            Bitmap bp = toImageActivity.scaleBitmap(strbitmap, toImageActivity.view_height, toImageActivity.view_width);
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setThumbnail(bp);
            imageEntity.setTextdada("none");
            imageEntityList.add(imageEntity);

            ColorFilter colorFilter = new ColorFilter();
            imageEntity = new ImageEntity();
            imageEntity.setThumbnail(colorFilter.Sepia_filter(bp));
            imageEntity.setTextdada("sepia");
            imageEntityList.add(imageEntity);
            customListAdapter = new CustomListAdapter(this, R.layout.customlist, imageEntityList);
            
            listView.setAdapter(customListAdapter);
        }else {
            Toast.makeText(this,"画像の取得に失敗しました",Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
