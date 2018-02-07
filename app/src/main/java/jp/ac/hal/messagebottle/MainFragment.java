package jp.ac.hal.messagebottle;

import android.app.ProgressDialog;
import android.content.Context;


import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import android.widget.ImageView;
import android.widget.Toast;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBBase;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBObjectService;
import com.nifty.cloud.mb.core.NCMBQuery;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private int mParam1;
    private String mParam2;
    private GridView gridView;
    private boolean resultFlg;
    private ProgressDialog mProgressDialog;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context;
    private View rootView;
    private List<NCMBObject> ncmbObjectList;
    private int USERCODE;

    private MainFragmentLisner mainFragmentLisner;
    //画像のurl
    private static final String imageurl = "https://mb.api.cloud.nifty.com/2013-09-01/applications/Tn5D08kBenUjNx1R/publicFiles/";

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(int param, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //リスト生成
        //taskExe();
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

        //リストから画像を選択
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            GridView gv = (GridView)parent;
            FileEntity fileEntity = (FileEntity)gv.getItemAtPosition(position);
            //一時保存
            Bitmap bp = fileEntity.getDetailImage();
            String filepath = changefile(bp).getAbsolutePath();

            String _objectId = fileEntity.getObject_id();
            switch (mParam1){
                case Userfragment.RECEPTIONCODE :
                    //お気に入りしているか確認
                    resultFlg = CheckFavorite(_objectId);
                    break;
                case Userfragment.SENDCODE:
                    //自分のメッセージ
                    _objectId = fileEntity.getObject_id();
                    break;
                default:
                    break;
            }
            mainFragmentLisner.OnShowChild(filepath, _objectId, resultFlg);
        });
    }

    private boolean CheckFavorite(String objectId) {
        resultFlg = false;
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Favorite");
        query.whereEqualTo("FavoriteID", objectId);
        query.findInBackground((List<NCMBObject> list, NCMBException e) -> {
            if(e != null){
                resultFlg = false;
                //エラー処理
            }else{
                resultFlg = true;
            }
        });
        return resultFlg;
    }

    @Override
    public void onResume() {
        taskExe();
        super.onResume();
    }

    //画像を一時ファイルに保存
    public static File changefile(Bitmap bp){
        //一時保存ファイル名
        String imageName =  "onetime.jpg";
        File imageFile = new File(MainActivity.Companion.getContext().getFilesDir(),imageName);
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

        List<FileEntity> filelist = null;
        //消去ボタン非表示

        switch (mParam1){
            case Userfragment.RECEPTIONCODE :
                //全てのメッセージ
                filelist = AllLoadList();
                break;
            case Userfragment.SENDCODE:
                //自分のメッセージ
                break;
            default:
                break;
        }
        //List<FileEntity> filelist = QueryLoad(0);
        CustomGridAdapter gridAdapter = new CustomGridAdapter(getActivity(), filelist);
        gridView.setAdapter(gridAdapter);

    }


    //ファイル名取得(検索条件なし)
    public List<FileEntity> AllLoadList() {
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
            //画像のパス
            fe.setFile(imageurl + obj.getString("file"));
            fe.setObject_id(obj.getString("objectId"));
            fe.setFile_genre(obj.getString("file_id"));
            filelist.add(fe);
        }
        return filelist;
    }

    public List<FileEntity> MyQuery() {
        List<FileEntity> filelist = new ArrayList<>();
        //すべてのデータ取得の場合NCMBObjectServiceを用いる
        NCMBQuery<NCMBObject> ncmbQuery = new NCMBQuery<> ("File");
        //ユーザ名と一致するもの
        ncmbQuery.whereEqualTo("UserName", MainActivity.Companion.getUser_name());
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("User");
        query.whereMatchesQuery("subKey", query);
        query.findInBackground((List<NCMBObject> list, NCMBException e) -> {
            for(NCMBObject item : list){
                FileEntity fe = new FileEntity();
                //画像のパス
                fe.setFile(imageurl + item.getString("file"));
                fe.setObject_id(item.getString("objectId"));
                fe.setFile_genre(item.getString("file_id"));
                filelist.add(fe);
            }
        });

        return filelist;
    }

    //ファイル名取得(検索条件あり)
    public List<FileEntity> QueryLoad(int genre_id) {
        //すべてのデータ取得の場合NCMBObjectServiceを用いる
        NCMBQuery<NCMBObject> ncmbQuery = new NCMBQuery ("File");
        List<FileEntity> filelist = new ArrayList<>();
        ncmbQuery.whereEqualTo("genre_id", genre_id );

        ncmbQuery.findInBackground((List<NCMBObject> list, NCMBException e) -> {
            if(e != null){
                //エラー処理
                Log.e("genreError","noload");
            }else{
                ncmbObjectList = list;
            }
        });
        if(ncmbObjectList != null) {
            for (NCMBObject obj : ncmbObjectList) {
                FileEntity fe = new FileEntity();
                //画像のパス
                fe.setFile(imageurl + obj.getString("file"));
                fe.setObject_id(obj.getString("objectId"));
                fe.setFile_genre(obj.getString("file_id"));
                fe.setFile_genre(LoadGenre(obj.getInt("genre_id")));
                Log.e("genreError", String.valueOf(obj.getInt("genre_id")));
                filelist.add(fe);
            }
        }
        return filelist;
    }

    public String LoadGenre(int genre_id){
        NCMBObject genreQuery = new NCMBObject ("Genre");
        final String[] result = {null};
        genreQuery.put("genre_id", genre_id);
        genreQuery.fetchInBackground((NCMBBase ncmbBase, NCMBException e) -> {
            if(e != null){

            }else {
                result[0] = ncmbBase.getString("genre_id");
            }
        });
        return result[0];
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
            if(context instanceof MainFragmentLisner){
                mainFragmentLisner = (MainFragmentLisner) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
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
    protected interface MainFragmentLisner{
        void OnShowChild(String filepath, String Object_id, boolean flg);
    }
}