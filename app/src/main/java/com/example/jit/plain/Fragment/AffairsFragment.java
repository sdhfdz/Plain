package com.example.jit.plain.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jit.core.ActionCallbackListener;
import com.example.jit.model.VillageValueBean;
import com.example.jit.plain.Activity.AffairDetailActivity;
import com.example.jit.plain.Adapter.AdpExpandableList;
import com.example.jit.plain.PApplication;
import com.example.jit.plain.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 村务公开
 * Created by Max on 2016/6/29.
 */
@ContentView(R.layout.fragment_main_affairs)
public class AffairsFragment extends Fragment {

    private Context context;
    private final static String TAG = "AffairsFragment";
    private List<String> parent = null;
    private Map<String, List<String>> map = null;
    private AdpExpandableList adpExpandableList;
    private PApplication pApplication;

    @ViewInject(R.id.elv_fragment_main_affairs)
    private ExpandableListView expandableListView;
    @ViewInject(R.id.iv_fragment_main_affairs)
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        context = inflater.getContext();
        view = x.view().inject(this, inflater, container);

        init();
        return view;
    }

    private void init() {
        initData();
        initListener();
    }

    private void initListener() {
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getActivity(), AffairDetailActivity.class);
                String name = adpExpandableList.getChild(groupPosition,childPosition)+"";
                int index = name.indexOf(",");
                intent.putExtra("name", name.substring(0,index));
//                Log.d(TAG,adpExpandableList.getRegionid());
                intent.putExtra("regionid",name.substring(index+1));
                startActivity(intent);
                return true;

            }
        });
    }

    private void initData() {
        expandableListView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        parent = new ArrayList<String>();
        map = new HashMap<String, List<String>>();

        getDataFromServer(parent, map);

    }

    /**
     * 从服务器获取数据
     *
     * @param parent ExpandableList的 parent 内容
     * @param map    ExpandableList 的 child 内容
     */
    private void getDataFromServer(final List<String> parent, final Map<String, List<String>> map) {


        pApplication = (PApplication) getActivity().getApplication();
        pApplication.getAppAction().getVillage(new ActionCallbackListener<List<VillageValueBean>>() {
            @Override
            public void onSuccess(List<VillageValueBean> data) {
//                Log.e(TAG,data.toString());
                expandableListView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                if (data.size() != 0) {

                    for (int i = 0; i < data.size(); i++) {
                        parent.add(data.get(i).getName());
                        List<String> list = new ArrayList<String>();
                        for (int j = 0; j < data.get(i).getVillage().size(); j++) {
                            list.add(data.get(i).getVillage().get(j).getName() + "," + data.get(i).getVillage().get(j).getId());
                        }
                        map.put(data.get(i).getName(), list);

                    }
                    adpExpandableList = new AdpExpandableList(context, parent, map);
                    expandableListView.setAdapter(adpExpandableList);
                }else {
                    expandableListView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(x.app(), message, Toast.LENGTH_SHORT).show();
                expandableListView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 图片的点击事件,点击重新获取数据
     *
     * @param view
     */
    @Event(R.id.iv_fragment_main_affairs)
    private void onclickIV(View view) {
        getDataFromServer(parent,map);
    }

}
