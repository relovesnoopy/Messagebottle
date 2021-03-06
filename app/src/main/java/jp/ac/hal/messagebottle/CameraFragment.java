package jp.ac.hal.messagebottle;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.Toast;

import com.nifty.cloud.mb.core.NCMBAcl;

import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.NCMBObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;




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
    private static final int REQUEST_PERMISSION = 2000;
    private static final int REQUEST_CODE = 2500;
    private static final int RESULT_IMAGE = 3000;
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
    private int genreid;

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

        choosebtn.setOnClickListener(v -> {

            //カメラの起動Intentの用意
            if (Build.VERSION.SDK_INT >= 23) {
                checkPermission();
            }
            else {
                //Intentを返す
                intentCamera = cameraIntent();
            }

            intentCamera = cameraIntent();

            // ギャラリー用のIntent作成
            Intent intentGallery;
            if (Build.VERSION.SDK_INT < 19) {
                intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
            } else {
                intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
                //intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
               // intentGallery.setType("image/*");
                intentGallery.setType("*/*");
            }

            //ChooserにGallaryのIntentとCameraのIntentを登録
            Intent intent = Intent.createChooser(intentGallery, "画像の選択");
            if(intentCamera != null){
                intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentCamera});
            }
            startActivityForResult(intent, REQUEST_CHOOSER);
        });

        uploadbtn.setOnClickListener((View v) -> {
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
                    //if (fileid < kari) {
                    fileid = c.getInt(c.getColumnIndex("filenum"));
                    // }
                    // 次のデータへ
                    b = c.moveToNext();
                }
                fileid++;
                //データベース追加更新
                ContentValues values = new ContentValues();
                values.put("filenum", fileid);
                db.insert("userfile", null, values);

                //NCMBデータストア書き込み
                NCMBObject fileObj = new NCMBObject("File");
                //ファイル名:ユーザ名 + fileid.jpg
                String UserName = MainActivity.Companion.getUser_name();
                fileObj.put("file", UserName + fileid + ".jpg");
                fileObj.put("file_id", fileid);
                fileObj.put("genre_id", genreid);
                fileObj.put("UserName", UserName);
                try {
                    fileObj.save();
                } catch (NCMBException e) {
                    e.printStackTrace();
                }
                NCMBObject userObj = new NCMBObject("User");
                userObj.put("UserName", UserName);
                userObj.put("pointer", fileObj);
                userObj.saveInBackground( e -> {
                    if (e != null) {
                        // 取得に失敗した場合の処理
                        Log.e("NCMB_ERROR","Nopointer");
                    } else{
                        // 取得に成功した場合の処理
                    }
                });

                //通信実施
                //アップロード処理
                final NCMBFile file = new NCMBFile(MainActivity.Companion.getUser_name() + fileid + ".jpg", dataByte, acl);
                file.saveInBackground(e ->{
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
                            //初期化
                            uploadflg = false;
                            genreid = 0;
                            iv.setImageResource(R.drawable.noimage);
                        }
                });
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            cameraIntent();
        } else{
            //拒否している場合
            requestLocationPermission();
        }
    }
    // 許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            Toast.makeText(getActivity(), "カメラ機能が無効です", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            //requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode){
            case REQUEST_CHOOSER:
                //dataがnullの場合はギャラリーではなくカメラからの取得と判定しカメラのUriを使う
                uri = (data != null ? data.getData() : cameraUri);

                if(uri == null) {
                    if(cameraUri != null){
                        uri = cameraUri;
                    } else {
                        // 取得失敗
                        Toast.makeText(getActivity(), "Error.retry.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // ギャラリーへ画像追加
                //MediaScannerConnection.scanFile(getActivity(), new String[]{uri.getPath()}, new String[]{"image/jpeg"}, null);
                // 画像を選択


                //FileスキームのURIに変換する
                Uri FileUri = Uri.parse("file://" + getPathFromUri(getContext(), uri));
                //非同期処理
                boolean isuri = new File(uri.toString()).exists();
                boolean isfileuri = new File(FileUri.toString()).exists();

                Single.create((SingleOnSubscribe<Uri>) emitter -> emitter.onSuccess(FileUri))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<Uri>(){
                            @Override
                            public void onSuccess(Uri uri) {
                                //画像選択後フィルター選択画面へ遷移
                                Intent intent = new Intent(getActivity(), ImageUploadActivity.class);
                                intent.putExtra("picture", MainFragment.changefile(resizeImage(uri)).getAbsolutePath());
                                //Activityの移動
                                startActivityForResult(intent, RESULT_IMAGE);
                                //アップロード許可フラグを立てる
                                uploadflg = true;
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        });


                break;
            case RESULT_IMAGE :
                if(resultCode == RESULT_OK) {
                    Log.d("log","Ok_image");
                    //Intentの受け取り
                    String strbitmap = (String) data.getSerializableExtra("image");
                    genreid = (int)data.getSerializableExtra("genre");
                    Bitmap bitmap = new ImageManage().scaleBitmap(strbitmap);
                    iv.setImageBitmap(bitmap);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public Bitmap resizeImage(Uri FileUri) {
        Bitmap bp = null;
        //画像の向き
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(FileUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bp = android.provider.MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FileUri);
            int width = bp.getWidth();
            int height = bp.getHeight();


            int exifR = getExifint(exifInterface, ExifInterface.TAG_ORIENTATION);
            int R = 0;
            switch (exifR) {
                case 1:
                    R = 0;
                    break;
                case 3:
                    R = 180;
                    break;
                case 6:
                    R = 90;
                    break;
                case 8:
                    R = 270;
                    break;
                default:
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(R);  //角度指定

            bp = Bitmap.createBitmap(bp, 0, 0, width, height, matrix, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return bp;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private int getExifint(ExifInterface ei, String tag) {
        return  Integer.parseInt(ei.getAttribute(tag));
    }

    public String getPathFromUri(final Context context, final Uri uri) {
        boolean isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        Log.e(TAG,"uri:" + uri.getAuthority());
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(
                    uri.getAuthority())) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    return "/stroage/" + type +  "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(
                    uri.getAuthority())) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }else if ("com.android.providers.media.documents".equals(
                    uri.getAuthority())) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                contentUri = MediaStore.Files.getContentUri("external");
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = { MediaStore.Files.FileColumns.DATA };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int cindex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(cindex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * URIをFileスキームのURIに変換する.
     * @param uri 変換前のURI  例) content://media/external/images/media/33
     * @return 変換後のURI     例) file:///storage/sdcard/test1.jpg
     */
    private Uri getFileSchemeUri(Uri uri){
        String path = getPath(uri);
        Uri fileSchemeUri = fileSchemeUri = Uri.fromFile(new File(path));
        Log.d("FileUri", String.valueOf(fileSchemeUri));
        return fileSchemeUri;
    }

    private String getPath(Uri uri) {
        String path = uri.toString();
        if (path.matches("^file:.*")) {
            return path.replaceFirst("file://", "");
        } else if (!path.matches("^content:.*")) {
            return path;
        }
        Context context = getContext();
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        if (cursor != null){
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                path = cursor.getString(0);
            }
            cursor.close();
        }
        return path;
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
