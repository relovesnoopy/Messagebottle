package jp.ac.hal.messagebottle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBObjectService;
import com.nifty.cloud.mb.core.NCMBRelation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView gridView;
    private static final String TAG = "MainActivity";
    private ProgressDialog mProgressDialog;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context;
    private View rootView;
    //画像のurl
    private static final String imageurl = "https://mb.api.cloud.nifty.com/2013-09-01/applications/Tn5D08kBenUjNx1R/publicFiles/";

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        //リスト生成
        taskExe();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SwipeRefreshLayoutを作成
        this.rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.context = rootView.getContext();
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        gridView = (GridView)rootView.findViewById(R.id.gridView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //リスト生成
        //taskExe();
        //リストから画像を選択
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridView gv = (GridView)parent;
                ImageEntity imageEntity = (ImageEntity)gv.getItemAtPosition(position);
                //一時保存
                Bitmap bp = imageEntity.getThumbnail();
                //Intentクラスのインスタンス化
                Intent intent = new Intent(getActivity(), toImageActivity.class);
                //転送情報をセット
                intent.putExtra("image", changefile(bp).getAbsolutePath());
                //Activityの移動
                startActivity(intent);
            }
        });

    }

    //画像を一時ファイルに保存
    public static File changefile(Bitmap bp){
        //一時保存ファイル名
        String imageName =  "onetime.jpg";
        File imageFile = new File(MainActivity.getContext().getFilesDir(),imageName);
        FileOutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            bp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //画像をアプリの内部領域に保存
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    /**
     * 引っ張った時の処理
     */
    @Override
    public void onRefresh() {
        // 引っ張ったタイミングでリストを最新に更新
        taskExe();
        //ぐるぐる止める
        mSwipeRefreshLayout.setRefreshing(false);

    }


    private void taskExe() {
        //アップロード進捗状況表示

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("画像取得...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            List<ImageEntity> listItems = new ArrayList<>();
            List<FileEntity> filelist = loadfileentity();
            @Override
            protected Void doInBackground(Void... params) {
                //画像取得
                //リストに追加
                for(FileEntity fe : filelist){
                    ImageEntity item = new ImageEntity();
                    item.setThumbnail(downloadImage(imageurl + fe.getFile()));
                    item.setNcmbImage(imageurl + fe.getFile());
                    listItems.add(item);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                CustomGridAdapter gridAdapter = new CustomGridAdapter(getActivity(), listItems);
                gridView.setAdapter(gridAdapter);
                mProgressDialog.dismiss();
            }
        };
        task.execute();
    }

    //ファイル名取得(検索条件なし)
    public List<FileEntity> loadfileentity() {
        //すべてのデータ取得の場合NCMBObjectServiceを用いる
        NCMBObjectService service = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        List<NCMBObject> list;
        try {
            list = service.searchObject("File", null);
        } catch (NCMBException e) {
            //エラー
            Toast.makeText(getActivity(), "Failed loading messages", Toast.LENGTH_LONG).show();
            return null;
        }
        List<FileEntity> filelist = new ArrayList<>();
        for (NCMBObject obj : list) {
            FileEntity fe = new FileEntity();
            fe.setFile(obj.getString("file"));
            fe.setObject_id(obj.getString("objectId"));
            fe.setFile_genre(obj.getString("file_id"));
            filelist.add(fe);
        }
        return filelist;
    }




    private Bitmap downloadImage(String address) {
        Bitmap bmp = null;
        try {
            URL url = new URL( address );
            // HttpURLConnection インスタンス生成
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            // リクエストメソッド
            urlConnection.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            urlConnection.setInstanceFollowRedirects(false);
            // ヘッダーの設定(複数設定可能)
            urlConnection.setRequestProperty("Accept-Language", "jp");
            // 接続
            urlConnection.connect();

            int resp = urlConnection.getResponseCode();
            switch (resp){
                case HttpURLConnection.HTTP_OK:
                    InputStream is = urlConnection.getInputStream();
                    bmp = BitmapFactory.decodeStream(is);
                    is.close();
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "画像のダウンロードに失敗しました"
            + address);
            e.printStackTrace();
        }

        return bmp;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
