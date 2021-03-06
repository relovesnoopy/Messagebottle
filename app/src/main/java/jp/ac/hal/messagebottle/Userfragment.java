package jp.ac.hal.messagebottle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Userfragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Userfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Userfragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private Context context;
    private Button loginbtn;
    private Button signupbtn;
    private Button SendMessageBtn;
    private Button ReceptionMessageBtn;
    private static final int USERCODE = 500;
    public static final int RECEPTIONCODE = 1000;
    public static final int SENDCODE = 1100;


    private OnFragmentInteractionListener mListener;

    public Userfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Userfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Userfragment newInstance(String param1, String param2) {
        Userfragment fragment = new Userfragment();
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
        this.rootView = inflater.inflate(R.layout.fragment_userfragment, container, false);
        this.context = rootView.getContext();
        loginbtn = (Button)rootView.findViewById(R.id.login);
        signupbtn = (Button)rootView.findViewById(R.id.sinup);
        SendMessageBtn = (Button)rootView.findViewById(R.id.Send_message);
        ReceptionMessageBtn = (Button)rootView.findViewById(R.id.Reception_message);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ログイン画面遷移
        loginbtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivityForResult(intent, USERCODE);
        });
        //新規登録
        signupbtn.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), SignupActivity.class);
            startActivityForResult(intent, USERCODE);
        });

        SendMessageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainContentsActivity.class);

            intent.putExtra("MESSAGE", SENDCODE);
            startActivity(intent);
        });

        ReceptionMessageBtn.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), MainContentsActivity.class);
            intent.putExtra("MESSAGE", RECEPTIONCODE);
            startActivity(intent);
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
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
    */

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
