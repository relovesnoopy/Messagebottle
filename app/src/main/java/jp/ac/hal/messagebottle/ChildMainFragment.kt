package jp.ac.hal.messagebottle

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.nifty.cloud.mb.core.NCMBObject
import com.nifty.cloud.mb.core.NCMBQuery
import com.squareup.picasso.Picasso


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChildMainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ChildMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChildMainFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var favoflg: Boolean = false
    private var mListener: ChildMainFragmentListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
            favoflg = arguments.getBoolean(ARG_PARAM3)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_child_main, container, false)
        val imageView = view.findViewById(R.id.ChildImage) as ImageView
        //imageView.setImageBitmap(ImageManage().scaleBitmap(mParam1!!))
        Picasso.with(context).load(mParam1!!).placeholder(R.drawable.noimage).error(R.drawable.noimage).into(imageView)
        imageView.setOnClickListener { mListener!!.onCloseChildMain() }

        val deleteBtn = view.findViewById(R.id.DeleteDispBtn) as Button
        if(!mParam2.equals("_object")){ deleteBtn.visibility = View.INVISIBLE }
        deleteBtn.setOnClickListener{ deleteMessage(mParam2!!)}

        val favoriteBtn = view.findViewById(R.id.favoriteBtn) as FloatingActionButton

        favoriteBtn.setOnClickListener({
            if(favoflg){
                //CheckFavorite(mParam2!!)
                deleteFavorite(mParam2!!)
                favoriteBtn.setImageResource(R.drawable.iconstar)
                !favoflg
            } else {
                favoriteBtn.setImageResource(R.drawable.favo)
                favoriteMessage(mParam2!!)
                !favoflg
            }
        })


        return view
    }

    private fun CheckFavorite(_objectID: String) : Boolean {
        var resultFlg = false
        // Favoriteクラスを検索するNCMBQueryを作成
        val innerQuery = NCMBQuery<NCMBObject>("Favorite")
        // UserNameフィールドの値でログインしているユーザ名と一致するデータを検索する条件を設定
        innerQuery.whereEqualTo("FavoriteID", _objectID)
        // Fileクラスを検索するNCMBQueryを作成
        val query = NCMBQuery<NCMBObject>("File")
        // innerQueryの条件と一致するデータをpointerフィールドが参照しているFavoriteクラスのデータから検索する条件を設定
        query.whereMatchesQuery("pointer", innerQuery)
        // 設定した条件で検索
        query.findInBackground { _, e ->
            resultFlg = e == null
        }
        return resultFlg
    }

    private fun favoriteMessage(_objectID: String){
        val ncmb = NCMBObject("File")
        ncmb.objectId = _objectID
        ncmb.save()
        val obj = NCMBObject("Favorite")
        obj.put("UserName", MainActivity.Companion.user_name)
        obj.put("FavoriteID", _objectID)
        obj.put("pointer", ncmb)
        obj.saveInBackground { e ->
            if (e != null) {
                // 取得に失敗した場合の処理
                Log.e("NCMB_ERROR","No_pointer")
            }
        }
    }

    private fun deleteFavorite(_objectID: String){
        val delFavo = NCMBQuery<NCMBObject>("Favorite")
        delFavo.whereEqualTo("FavoriteID", _objectID)
        delFavo.findInBackground { objects, e ->
            if (e != null){
                // 検索失敗時の処理
                e.printStackTrace()
            } else{
                // 検索成功時の処理
                val obj = NCMBObject("Favorite")
                for (item in objects){
                    obj.objectId = item.getString("objectId")
                    obj.fetchInBackground{obj2, e ->
                        if (e != null) {
                            // 取得に失敗した場合の処理
                            e.printStackTrace()
                        } else {
                            // 取得に成功した場合の処理
                            if( obj2 is NCMBObject){
                                obj2.deleteObject()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteMessage(_objectID: String) {
        //削除処理
        val obj = NCMBObject("File")
        obj.objectId = _objectID
        obj.fetchInBackground{ obj2 , e ->
            if (e != null) {
                // 取得に失敗した場合の処理
                e.printStackTrace()
            } else {
                // 取得に成功した場合の処理
                if( obj2 is NCMBObject){
                    obj2.deleteObjectInBackground { e2 ->
                        if (e2 != null) {
                            e2.printStackTrace()
                        } else {
                            //モーダルウィンドウを閉じる
                            mListener?.onCloseChildMain()
                        }
                    }
                }
            }
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onCloseChildMain()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ChildMainFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    interface ChildMainFragmentListener {
        fun onCloseChildMain()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        private val ARG_PARAM3 = "param3"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param filepath Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChildMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(filepath: String, _objectID: String, boolean: Boolean): ChildMainFragment {
            val fragment = ChildMainFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, filepath)
            args.putString(ARG_PARAM2, _objectID)
            args.putBoolean(ARG_PARAM3, boolean)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
