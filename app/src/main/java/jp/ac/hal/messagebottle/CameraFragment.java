package jp.ac.hal.messagebottle;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FetchFileCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBAcl;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;

import static android.app.Activity.RESULT_OK;
import static jp.ac.hal.messagebottle.MainActivity.getContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int REQUEST_CHOOSER = 1000;
    private static final  int REQUEST_PERMISSION = 2000;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton choosebtn;
    private Button uploadbtn;
    private Uri uri;
    private ImageView iv;
    private ProgressDialog mProgressDialog;
    private boolean uploadflg;
    private Intent intentCamera;
    private Uri cameraUri;
    private File  cameraFile;
    private String filePath;
    private  ImageButton imgb2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //画像選択,アップロードボタンインスタンス化
        uploadbtn= (Button)view.findViewById(R.id.upload);
        choosebtn= (FloatingActionButton)view.findViewById(R.id.floatingActionButton);
        //表示用ImageViewインスタンス化
        iv = (ImageView)view.findViewById(R.id.imageView);
        imgb2 = (ImageButton)view.findViewById(R.id.imageButton2);

        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //カメラの起動Intentの用意
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                }
                else {
                    //Intentを返す
                    intentCamera = cameraIntent();
                }

                // ギャラリー用のIntent作成
                Intent intentGallery;
                if (Build.VERSION.SDK_INT < 19) {
                    intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                    intentGallery.setType("image/*");
                } else {
                    intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
                    intentGallery.setType("image/*");
                }

                //ChooserにGallaryのIntentとCameraのIntentを登録
                Intent intent = Intent.createChooser(intentGallery, "画像の選択");
                if(intentCamera!=null){
                    intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentCamera});
                }
                startActivityForResult(intent, REQUEST_CHOOSER);
            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!uploadflg){
                    Toast.makeText(getActivity(), "画像が選択されていません", Toast.LENGTH_LONG).show();
                } else {
                    //アップロード進捗状況表示
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setMessage("アップロード中です...");
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.show();
                    Bitmap bp = ((BitmapDrawable) iv.getDrawable()).getBitmap();

                    //ファイルのアップロード
                    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                    bp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream);
                    byte[] dataByte = byteArrayStream.toByteArray();

                    //読み込み 書き込み 許可
                    NCMBAcl acl = new NCMBAcl();
                    acl.setPublicReadAccess(true);
                    acl.setPublicWriteAccess(true);

                    //データベース取得,データベース接続
                    DBOpenHelper dbh = new DBOpenHelper(getActivity());
                    SQLiteDatabase db = dbh.getWritableDatabase();
                    // 抽出する列名(フィールド名)をString型の配列で記述する
                    String[] col = {"_id", "filenum"};

                    // SQLの実行
                    Cursor c = db.query("userfile", col, null, null, null, null, null);

                    // カーソルを先頭データへ
                    boolean b = c.moveToFirst();
                    int fileid = 0;
                    while (b) {
                        int f_num = c.getInt(c.getColumnIndex("filenum"));
                        //if (fileid < kari) {
                        fileid = f_num;
                        // }
                        // 次のデータへ
                        b = c.moveToNext();
                    }
                    fileid++;
                    //データベース追加更新
                    ContentValues values = new ContentValues();
                    values.put("filenum", 0 + fileid);
                    db.insert("userfile", null, values);

                    //NCMBデータストア書き込み
                    NCMBObject fileObj = new NCMBObject("File");
                    //ファイル名:ユーザ名 + fileid.jpg
                    fileObj.put("file", "testUser" + fileid + ".jpg");
                    fileObj.put("file_id", fileid);
                    fileObj.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "送信に失敗しました", Toast.LENGTH_LONG).show();
                                return;
                            }
                            // update all messages
                        }
                    });

                    //通信実施
                    //アップロードするファイル名
                    final NCMBFile file = new NCMBFile("testUser" + fileid + ".jpg", dataByte, acl);
                    file.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e != null) {
                                //保存失敗
                                mProgressDialog.dismiss();
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Error")
                                        .setMessage("アップロードエラー:" + e.getMessage())
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                //アップロード通知
                                //画像初期化
                                mProgressDialog.dismiss();
                                new AlertDialog.Builder(getActivity()).setTitle("Up Load")
                                        .setMessage("アップロード完了")
                                        .setPositiveButton("OK", null)
                                        .show();
                                iv.setImageResource(R.drawable.abc);
                                //フィルター画像不可視化
                                imgb2.setVisibility(View.GONE);
                                uploadflg = false;
                            }
                        }
                    });
                }

            }
        });
        imgb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bp = ((BitmapDrawable)imgb2.getDrawable()).getBitmap();
                iv.setImageBitmap(bp);
            }
        });


    }

    private Intent cameraIntent(){
        // 保存先のフォルダーを作成
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG");
        cameraFolder.mkdirs();
        // 保存ファイル名
        String photoName = System.currentTimeMillis() + ".jpg";
        filePath = cameraFolder.getPath() +"/" + photoName ;
        // 画像のファイルパス
        cameraFile = new File(filePath);
        cameraUri = Uri.fromFile(cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        return intent;
    }

    // 許可チェック
    private void checkPermission(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            cameraIntent();
        } else{
            //拒否している場合
            requestLocationPermission();
        }
    }
    // 許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        } else {
            Toast.makeText(getActivity(), "カメラ機能が無効です", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CHOOSER) {

            if(resultCode != RESULT_OK) {
                // キャンセル時
                return ;
            }

            //dataがnullの場合はギャラリーではなくカメラからの取得と判定しカメラのUriを使う
            uri = (data != null ? data.getData() : cameraUri);

            if(uri == null) {
                // 取得失敗
                Toast.makeText(getActivity(), "Error.retry.", Toast.LENGTH_LONG).show();
                return;

            }

            // ギャラリーへ画像追加
            //MediaScannerConnection.scanFile(getActivity(), new String[]{uri.getPath()}, new String[]{"image/jpeg"}, null);

            // 画像を設定
            try {
                Bitmap bp = android.provider.MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                iv.setImageBitmap(bp);
                bp = null;

                bp = ((BitmapDrawable)iv.getDrawable()).getBitmap();
                ColorFilter colorFilter = new ColorFilter();
                //画像選択後フィルター選択画面へ遷移
                Intent intent = new Intent(getActivity(), ImageUploadActivity.class);
                intent.putExtra("picture", MainFragment.changefile(bp).getAbsolutePath());
                //Activityの移動
                startActivity(intent);
                //imgb2.setImageBitmap(colorFilter.Sepia_filter(bp));
                //imgb2.setVisibility(View.VISIBLE);

                //アップロード許可フラグを立てる
                uploadflg = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
