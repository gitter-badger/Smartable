package com.eagzzycsl.smartable;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

import common.Business;
import common.MyTime;
import common.MyUtil;
import database.DatabaseManager;


public class AddActivity extends ActionBarActivity {
    // 四个分别显示起始结束时间的TextView
    private AppCompatTextView textView_startDate;
    private AppCompatTextView textView_startTime;
    private AppCompatTextView textView_endDate;
    private AppCompatTextView textView_endTime;
    private Toolbar toolbar_add;
    private AppCompatEditText editText_location;
    private AppCompatEditText editText_title;
    // 切换是否全天的switch，不过是compat中的switch所以名字里带着compat，为的是5.0的样式
    private SwitchCompat switch_allDay;
    // 设置提醒时间的TextView
    private AppCompatTextView textView_alert;
    // 预置两个字符串，用于显示提醒，一个是全天的活动，一个是非全天的活动，到时候这个应该是从数据库中读取
    private String[] noAllDayAlert = new String[]{"无通知", "在活动开始时", "提前30分钟"};
    private String[] allDayAlert = new String[]{"无通知", "当天9:00",
            "提前一天（23:00）"};
    // 一个变量，用到时再解释。
    private String[] alertItems = noAllDayAlert;
    private String FinalFlag, FinalFlag_1, location_title, title;

    static Boolean switch_allDay_zhuangtai = false;

    private MyTime timeStart = new MyTime();
    private MyTime timeEnd = new MyTime();
    private String opt;
    private int Business_id;

    private void myIni() {
        textView_startDate.setText(MyPickerDialog.getDate(timeStart.getYear(),
                timeStart.getMonth(), timeStart.getDay()));
        textView_endDate.setText(MyPickerDialog.getDate(timeEnd.getYear(),
                timeEnd.getMonth(), timeEnd.getDay()));
        textView_startTime.setText(MyPickerDialog.getMoment(timeStart.getHour(), timeStart.getMinute()));
        textView_endTime.setText(MyPickerDialog.getMoment(timeEnd.getHour(), timeEnd.getMinute()));
        textView_alert.setText(alertItems[2]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        myFindViewById();//findView
        mySetView();//给view设置侦听等
        Bundle bundle = getIntent().getExtras();
        opt = bundle.getString("opt");

        switch (opt) {
            case "add":
                //算法有问题，很容易出现25小时的问题
                //还有一个是小时的那个下标问题到底哪边处理的好？目前这边处理好了
                timeStart = new MyTime(bundle.getInt("year"),
                        bundle.getInt("month") + 1,
                        bundle.getInt("day"),
                        bundle.getInt("hour"),
                        0);
                timeEnd = new MyTime(bundle.getInt("year"),
                        bundle.getInt("month") + 1,
                        bundle.getInt("day"),
                        bundle.getInt("hour") + 1,
                        0);

                break;
            case "add_withClass":

                editText_location.setText(bundle.getString("location_title"));
                timeStart = new MyTime(bundle.getInt("year"),
                        bundle.getInt("month") + 1,
                        bundle.getInt("day"),
                        bundle.getInt("hour"),
                        0);
                timeEnd = new MyTime(bundle.getInt("year"),
                        bundle.getInt("month") + 1,
                        bundle.getInt("day"),
                        bundle.getInt("hour") + 1,
                        0);
                //add in by kind view
                break;
            case "edit":
                location_title = bundle.getString("location_title");
                title = bundle.getString("item_value");
                editText_location.setText(location_title);
                editText_title.setText(title);

                break;
            case "edit_withId":
                int id = Integer.valueOf(bundle.getString("id"));
                Business_id = id;
                DatabaseManager dm = DatabaseManager.getInstance(AddActivity.this);

                Business bs = dm.getBusiness(id);
                timeStart = bs.getStart();
                timeEnd = bs.getEnd();
                editText_title.setText(bs.getTitle());

                dm.close();
                break;
        }
        myIni();//一些初始化操作
    }

    // 有关菜单的操作
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate表示把布局转化为view，不是很懂
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 改成switch By宇
        switch (id) {
            case R.id.action_settings: {
                return true;
            }
            case R.id.action_save: {
                DatabaseManager databaseManager = DatabaseManager.getInstance(AddActivity.this);
                databaseManager.insert_add_table("add_table", (getValues())[0]);
                databaseManager.close();
                Toast.makeText(this, "恭喜主人，您成功加入了一件事！", Toast.LENGTH_SHORT).show();
                //EAGzzyCSL
                DatabaseManager.getInstance(AddActivity.this).insertBusiness(new Business(editText_title.getText().toString(), timeStart, timeEnd).toContentValues());
                finish();
                return true;
            }
            case R.id.action_delete: {
                DatabaseManager databaseManager = DatabaseManager.getInstance(AddActivity.this);
                String[] data = {title, location_title};
//                if (FinalFlag.equals("FragmentByKind_ListView")) {
                if (opt.equals("edit")) {
                    databaseManager.delete_thing("add_table", "title = ? and location = ?", data);

                    Toast.makeText(this, "恭喜主人，您成功删除了一件事！", Toast.LENGTH_SHORT).show();
                }
                if (opt.equals("edit_withId")) {
                    databaseManager.deleteBusiness(Business_id);
                    Toast.makeText(this, "您成功删除了一件事！", Toast.LENGTH_SHORT).show();
                }
                databaseManager.close();
                finish();
                return true;
            }
            case android.R.id.home: {
                // 此处可能会涉及到安卓的activity的栈的东西
                finish();
                return true;
            }
        }
        // 此处加代码设置彩电上别的内容的点击事件
        return super.onOptionsItemSelected(item);
    }

    private void myFindViewById() {
        toolbar_add = (Toolbar) findViewById(R.id.toolBar_add);
        textView_startDate = (AppCompatTextView) findViewById(R.id.textView_startDate);
        textView_startTime = (AppCompatTextView) findViewById(R.id.textView_startTime);
        textView_endDate = (AppCompatTextView) findViewById(R.id.textView_endDate);
        textView_endTime = (AppCompatTextView) findViewById(R.id.textView_endTime);
        switch_allDay = (SwitchCompat) findViewById(R.id.switch_allDay);
        textView_alert = (AppCompatTextView) findViewById(R.id.textView_alert);
        editText_location = (AppCompatEditText) findViewById(R.id.edit_location);
        editText_title = (AppCompatEditText) findViewById(R.id.main_editText_title);
    }

    private void mySetView() {
        setSupportActionBar(toolbar_add);// 把toolbar设置为actionbar，这样toolbar就和actionbar一样用了，起码目前我没有发现有大的影响或者差别
        getSupportActionBar().setDisplayShowHomeEnabled(true);// 设置toolbar左侧的图标显示
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 设置将它显示为一个返回键
        getSupportActionBar().setHomeButtonEnabled(true);// 设置它可以响应事件
        textView_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 大意：显示一个dialog，
                // 详解：dialog我只会这一个所以就只能说这个一个，显示一个AlertDialog的方法是先创建一个AlertDialog的builder，即下面的new，然后为这个builder设置内容，即下面的setSingleChoiceItems，表示单选项目，
                // 最后调用show方法来显示这个dialog，或者先调用create方法创建一个dialog然后show，如果没有create方法而直接掉show方法的话show会自己调用create；
                // setSingleChoiceItems的参数，第一个是显示的项目，第二个是表示默认选中第几个，第三个是单击的事件
                // alertItems为要显示的内容项，造这么一个变量是为了不每次去选择填充全体那个数组还是段时间的那个数组，只要用这个一个变量就可以搞定了，在下面的switch事件中修改这个变量的值
                // Arrays.asList(alertItems).indexOf(textView_alert.getText().toString())表示先从alertItems转换一个List出来然后调用list的indexOf方法获取textView_alert的内容在数组中的下标
                // 之所以这么做是因为数据没有indexOf方法而list有，这么做实现的是当点击修改一个提醒时默认选中的就是文本内容上显示的那个提醒
                (new AlertDialog.Builder(AddActivity.this))
                        .setSingleChoiceItems(
                                alertItems,
                                Arrays.asList(alertItems).indexOf(
                                        textView_alert.getText().toString()),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 点击后把文本内容更改，witch表示点击的是哪个
                                        textView_alert
                                                .setText(alertItems[which]);
                                        // dismiss方法dialog
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });
        switch_allDay
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // 全天的switch在切换时执行显示隐藏起始结束时间的操作，同时为alertItems更改赋值
                        switch_allDay_zhuangtai = isChecked;
                        if (isChecked) {
                            textView_startTime.setVisibility(View.INVISIBLE);
                            textView_endTime.setVisibility(View.INVISIBLE);
                            alertItems = allDayAlert;
                        } else {
                            alertItems = noAllDayAlert;
                            textView_startTime.setVisibility(View.VISIBLE);
                            textView_endTime.setVisibility(View.VISIBLE);
                        }
                        // 当事件被切换为全天或者段时间时它的默认提醒为第三个，不过下标为2，即提前30分钟或者提前一天
                        textView_alert.setText(alertItems[2]);
                    }
                });

        textView_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyDatePickerDialog(AddActivity.this, textView_startDate, timeStart)
                        .show();
            }
        });
        textView_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyDatePickerDialog(AddActivity.this, textView_endDate, timeEnd)
                        .show();
            }
        });
        textView_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTimePickerDialog(AddActivity.this, textView_startTime, timeStart)
                        .show();
            }
        });
        textView_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTimePickerDialog(AddActivity.this, textView_endTime, timeEnd)
                        .show();
            }
        });
    }


    // 要传数据库之前，把所有数据打包的函数。 by宇
    public ContentValues[] getValues() {
        // findView
        AppCompatEditText editText_title = (AppCompatEditText) findViewById(R.id.main_editText_title);
        AppCompatEditText edit_detail = (AppCompatEditText) findViewById(R.id.edit_detail);
        // SwitchCompat switch_allDay = (SwitchCompat)
        // findViewById(R.id.switch_allDay);
        // AppCompatTextView textView_startDate = (AppCompatTextView)
        // findViewById(R.id.textView_startDate);
        // AppCompatTextView textView_endDate = (AppCompatTextView)
        // findViewById(R.id.textView_endDate);
        // AppCompatTextView textView_startTime = (AppCompatTextView)
        // findViewById(R.id.textView_startTime);
        // AppCompatTextView textView_endTime = (AppCompatTextView)
        // findViewById(R.id.textView_endTime);
        AppCompatEditText edit_classify = (AppCompatEditText) findViewById(R.id.edit_classify);
        AppCompatEditText edit_location = (AppCompatEditText) findViewById(R.id.edit_location);
        AppCompatTextView textView_alert = (AppCompatTextView) findViewById(R.id.textView_alert);

        // 初始化，可能会有“空指针异常”， 暂且未写

        // 获取值
        String title_value = editText_title.getText().toString();
        String detail_value = edit_detail.getText().toString();
        Boolean switch_allDay_value = switch_allDay_zhuangtai;// 没有把Boolean的值转化为字符串，这样真的好吗
        String startDate_value = textView_startDate.getText().toString();// java
        // 语法中有一个是能够把“用逗号分开的字符串”一个一个切开，这里暂且搁置，等成功插入数据库后，看下数据库中存储的样式是什么
        String endDate_value = textView_endDate.getText().toString();
        String startTime_value = textView_startTime.getText().toString();
        String endTime_value = textView_endTime.getText().toString();
        String classify_value = edit_classify.getText().toString();
        String location_value = edit_location.getText().toString();
        String alert_value = textView_alert.getText().toString();// 下面传入的时候
        // Integer.parseInt()会报错，于是改掉了？？

        // 键值对打包开始
        ContentValues add_values = new ContentValues();
        add_values.put("title", title_value);
        add_values.put("detail", detail_value);
        add_values.put("isRemind", switch_allDay_value);
        add_values.put("remind_early", alert_value);
        add_values.put("location", location_value);
        add_values.put("classify", classify_value);
        add_values.put("time_start", startTime_value);
        add_values.put("time_end", endTime_value);
        add_values.put("date_start", startDate_value);
        add_values.put("date_end", endDate_value);

        return new ContentValues[]{add_values};
    }
}


