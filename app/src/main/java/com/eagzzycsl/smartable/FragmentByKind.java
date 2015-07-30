package com.eagzzycsl.smartable;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.DatabaseManager;
import view.FlowLayout;

public class FragmentByKind extends Fragment {
    //分类界面融合入框架 - 前期定义 By宇
    SimpleAdapter[] adapter = new SimpleAdapter[50];// 自定义适配器， 将【数组】data 里的东西 放进【容器】ListView

    private final ListView[] listContent = new ListView[50];// 容器填东西
    private final TextView[] textview = new TextView[50];// 各个小标题
    private TextView textview11;// 各个小标题
    private View[] child_classify = new View[30];
    private FlowLayout mFlowLayout;
    private ImageButton[] add_button = new ImageButton[50];
    private int i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_kind, container, false);

        mFlowLayout = (FlowLayout) v.findViewById(R.id.id_flowlayout);

        //分类界面融合入框架 - 主函数中 By宇
        DatabaseManager databaseManager = DatabaseManager.getInstance(getActivity());//连接DatabaseManager
        DatabaseManager.query_classify query_classify = databaseManager.new query_classify();//得到内部类对象

        //首先判断数据库中是否有数据，如果没有，就提醒用户去加！！
        //TODO 对于没有地点(分类)的事项，还没打印出来
        //TODO 还不能动态生成listContent
        if (-1 == query_classify.getLocaNum()) {
            Toast.makeText(getActivity(), "目前还没有任何事项，主人快点加点事情吧！", Toast.LENGTH_SHORT).show();
        } else {
            for (i = 0; i < query_classify.getLocaNum(); i++) {

                LinearLayout.LayoutParams polish_classify = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );

                //TODO child_classify一定要开数组吗？
                child_classify[i] = View.inflate(getActivity(), R.layout.classify_list, null);
                mFlowLayout.addView(child_classify[i], polish_classify);

                listContent[i] = (ListView) child_classify[i].findViewById(R.id.listContent1);
                textview[i] = (TextView) child_classify[i].findViewById(R.id.classify_stitle1);

                //各个小标题
                //TODO 标题过长时，不能够全部显示出来
                textview[i].setText(query_classify.getLocaTitle().get(i));//

                //listContent 填数据
                adapter[i] = new SimpleAdapter(getActivity(), getData(i), R.layout.simpleadapter, new String[]{"btn_check_off_normal_holo_light", "title"},
                        new int[]{R.id.btn_check_off_normal_holo_light, R.id.title});//适配器装填数据 , 把填充的 data 数组里的值打印到 【自定义】 ListView 里
//                listContent1 = (ListView) v.findViewById(R.id.listContent1);
                listContent[i].setAdapter(adapter[i]);

                //add_button点击事件“功能绑定”
                add_button[i] = (ImageButton) child_classify[i].findViewById(R.id.ic_suggestions_add);
                add_button[i].setOnClickListener(listener_add_button);

                //add_button 点击事件“效果绑定”
                add_button[i].setOnTouchListener(listener_OnTouch);

                //ListView点击事件
                listContent[i].setOnItemClickListener(listener_ListView);
            }
        }
        databaseManager.close();//这个界面要多次访问数据库，因此不在DatabaseManager中关闭数据库

        return v;
    }


    //分类界面融合入框架 - 自定义函数  By宇
    private List<Map<String, Object>> getData(int roll) {
        //以“地点”作为测试
        DatabaseManager databaseManager = DatabaseManager.getInstance(getActivity());//连接DatabaseManager
        DatabaseManager.query_classify query_classify = databaseManager.new query_classify();//得到内部类对象

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int j = 0; j < query_classify.getLocaNum((query_classify.getLocaTitle()).get(roll)); j++) {//0
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("btn_check_off_normal_holo_light", R.drawable.btn_check_off_normal_holo_light);
            map.put("title", (query_classify.getLocaName(query_classify.getLocaTitle().get(roll))).get(j));

            list.add(map);
        }
        return list;
    }


    //点击事件处理，获取是哪个块里的事件,打包数据；并跳转到add界面，在add界面写好接收title
    private View.OnClickListener listener_add_button = new View.OnClickListener() {
        //TODO 研究：采用标记的方法记录每个“块”的位置
        private int index2;
        @Override
        public void onClick(View v) {

            DatabaseManager databaseManager = DatabaseManager.getInstance(getActivity());//连接DatabaseManager
            DatabaseManager.query_classify query_classify = databaseManager.new query_classify();//得到内部类对象

            Intent intent = new Intent(getActivity(), AddActivity.class);

            //打包数据
            try {
                Bundle allBundle = new Bundle();
                String FinalFlag = "FragmentByKind";
//                allBundle.putString("FinalFlag", FinalFlag);
                allBundle.putString("opt", "add_withClass");
                Calendar c=Calendar.getInstance();
                allBundle.putInt("year", c.get(Calendar.YEAR));
                allBundle.putInt("month",c.get(Calendar.MONTH));
                allBundle.putInt("day",c.get(Calendar.DAY_OF_MONTH));
                allBundle.putInt("hour",c.get(Calendar.HOUR_OF_DAY));
                allBundle.putInt("minute", c.get(Calendar.MINUTE));
                //获取add_button所在“块”的坐标
                int index = ((ViewGroup) v.getParent().getParent().getParent().getParent()).indexOfChild((ViewGroup) v.getParent().getParent().getParent());
                allBundle.putString("location_title", query_classify.getLocaTitle().get(index));



                //TODO 日后这边会加好几个属性

                intent.putExtras(allBundle);
                startActivityForResult(intent, 1);
//                getActivity().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    //给ImageButton 添加“按下”的效果
    private View.OnTouchListener listener_OnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //更改为按下时的背景颜色
                v.setBackgroundColor(Color.parseColor("#E6E6E6"));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //改为抬起时的背景颜色
                v.setBackgroundColor(Color.parseColor("#D5D5D5"));
            }
            return false;
        }
    };


    //ListView点击事件
    private AdapterView.OnItemClickListener listener_ListView=new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            DatabaseManager databaseManager = DatabaseManager.getInstance(getActivity());//连接DatabaseManager
            DatabaseManager.query_classify query_classify = databaseManager.new query_classify();//得到内部类对象

            Intent intent = new Intent(getActivity(), AddActivity.class);

            //打包数据
            try {
                Bundle allBundle = new Bundle();
                String FinalFlag = "FragmentByKind_ListView";//与“所跳转页面”的暗号
//                allBundle.putString("FinalFlag", FinalFlag);
                allBundle.putString("opt", "edit");
                //获取listView所在“块”的坐标
                int index = ((ViewGroup)parent.getParent().getParent().getParent().getParent()).indexOfChild((ViewGroup) parent.getParent().getParent().getParent());
//                ((ViewGroup) view.getParent().getParent().getParent()).indexOfChild((ViewGroup) view.getParent().getParent());
                allBundle.putString("location_title", query_classify.getLocaTitle().get(index));

                //获取listView里TextView的值
                HashMap<String,String> item_value_parent= (HashMap<String, String>) parent.getItemAtPosition(position);
                String item_value = item_value_parent.get("title");
                allBundle.putString("item_value", item_value);
                Log.i("TAG","_____"  + item_value);
                //TODO 日后这边会加好几个属性

                intent.putExtras(allBundle);
                startActivityForResult(intent, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}