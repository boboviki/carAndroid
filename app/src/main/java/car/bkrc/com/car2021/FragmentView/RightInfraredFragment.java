package car.bkrc.com.car2021.FragmentView;

import android.content.Context;
import android.os.Bundle;
//import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import car.bkrc.com.car2021.R;
import car.bkrc.com.car2021.ViewAdapter.InfrareAdapter;
import car.bkrc.com.car2021.ViewAdapter.Infrared_Landmark;

import java.util.ArrayList;
import java.util.List;

public class RightInfraredFragment extends Fragment {

    public static final String TAG = "RightInfraredFragment";
    private Infrared_Landmark[]  infrareds = {
            new Infrared_Landmark("烽火台报警标志物", R.mipmap.alarm),
            new Infrared_Landmark("智能路灯标志物", R.mipmap.gear_position),
            new Infrared_Landmark("立体显示标志物", R.mipmap.stereo_display)
    };

    private List<Infrared_Landmark> InfraredList = new ArrayList<>();
    private static RightInfraredFragment mInstance =null;

 //   private RightInfraredFragment(){}
    public  static RightInfraredFragment getInstance() {
        if(mInstance ==null) {
            synchronized (RightInfraredFragment.class) {
                if(mInstance ==null) {
                    mInstance =new RightInfraredFragment();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.right_infrared_fragment, container, false);
        initInfrared();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        InfrareAdapter adapter = new InfrareAdapter(InfraredList, getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void initInfrared() {
        InfraredList.clear();
        for (int i = 0; i < infrareds.length; i++) {
            InfraredList.add(infrareds[i]);
        }
    }

}
