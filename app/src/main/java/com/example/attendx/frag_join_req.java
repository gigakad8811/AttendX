package com.example.attendx;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link frag_join_req#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frag_join_req extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView join_req_rv;
    RecyclerView.Adapter adapter;

    ImageView back_btn;

    String room_id = " ";

    BubbleNavigationLinearView bubbleNavigationLinearView;

    ArrayList<JoinReqHelper> join_req_arr = new ArrayList<>();

    public frag_join_req() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag_join_req.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_join_req newInstance(String param1, String param2) {
        frag_join_req fragment = new frag_join_req();
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
        View v = inflater.inflate(R.layout.frag_join_req_frag, container, false);

        return v;
    }
}