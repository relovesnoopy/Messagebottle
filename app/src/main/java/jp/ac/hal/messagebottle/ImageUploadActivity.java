package jp.ac.hal.messagebottle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBObjectService;
import java.util.List;


public class ImageUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        //Intentの受け取り
        String strbitmap = (String)getIntent().getSerializableExtra("picture");
        if(strbitmap != null){
            //Bitmap bp = toImageActivity.scaleBitmap(strbitmap, toImageActivity.view_height, toImageActivity.view_width);
            Bitmap bp = new ImageManage().scaleBitmap(strbitmap);
            final ImageView imageView = (ImageView)findViewById(R.id.select_image);
            imageView.setImageBitmap(bp);
            //タグ取得

            //Spinnerにセット
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
            //Spinnerのインスタンス化
            final Spinner sp = (Spinner)findViewById(R.id.spinner);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
/*
            NCMBObjectService objectService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
            try {
                List<NCMBObject> objectList = objectService.searchObject("Genre", null);
                for (NCMBObject object : objectList){
                    //タグ名取得
                    arrayAdapter.add(object.getString("genre_name"));
                }
                sp.setAdapter(arrayAdapter);
            } catch (NCMBException e) {
                //エラー
                Toast.makeText(this, "Failed loading messages", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return;
            }
*/

            //フィルター一覧をアダプターにセット
            ColorFilter colorFilter = new ColorFilter();
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            final CasarealRecycleViewAdapter adapter = new CasarealRecycleViewAdapter(colorFilter.getFilterList(bp));
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener((view, position) -> imageView.setImageBitmap(position.getThumbnail()));
            Button btn = (Button) findViewById(R.id.nextPage);
            final Intent intent = new Intent();


            btn.setOnClickListener(v -> {
                //画像とジャンルをセット
                intent.putExtra("image", MainFragment.changefile(((BitmapDrawable)imageView.getDrawable()).getBitmap()).getAbsolutePath());
                //intent.putExtra("genre", (String)sp.getSelectedItem());
                intent.putExtra("genre", sp.getSelectedItemPosition());
                setResult(RESULT_OK, intent);
                finish();
            });


        }else {
            Toast.makeText(this,"画像の取得に失敗しました",Toast.LENGTH_SHORT).show();
            finish();
        }


    }
}
