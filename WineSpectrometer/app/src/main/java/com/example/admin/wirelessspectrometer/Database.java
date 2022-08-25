package com.example.admin.wirelessspectrometer;


import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class Database extends android.support.v4.app.Fragment {

    ContactinfoDao mContactinfoDao;
    ListView listView;
    private MydbHeper mMydbHeper;
    private SQLiteDatabase sqLiteDatabase;
    private SimpleCursorAdapter adapter;
    CallBackValue callBackValue;                //自定义

    @Override
    public void onAttach(Activity activity) {           //fragment Activity传输数据
        super.onAttach(activity);
        callBackValue = (CallBackValue) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_database,container,false);
        listView = view.findViewById(R.id.lvdatabase);
        mMydbHeper = new MydbHeper(view.getContext());
        sqLiteDatabase = mMydbHeper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("contactinfo",null,null,null,null,null,null);
        String[] from = {"_id","name","laser"};
        int[] to = {R.id.textview1,R.id.textview2,R.id.textview3};
        adapter = new SimpleCursorAdapter(view.getContext(),R.layout.fragment_database,cursor,from,to);
        listView.setAdapter(adapter);

        // 长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view1, int i, final long l) {   //i position；l id

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setMessage("确定删除？");
                builder.setTitle("提示");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("test", String.format("i = %d",i));
                        Log.i("test", String.format("l = %d",l));
                        Toast.makeText(view1.getContext(),"删除"+i+"列",Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();         //刷新内容
                        mContactinfoDao = new ContactinfoDao(view.getContext());
                        int row_del = mContactinfoDao.deleteData((int) l);
                        if (row_del == -1){
                            Toast.makeText(view1.getContext(),"删除失败",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(view1.getContext(),"成功删除第"+row_del+"行",Toast.LENGTH_LONG).show();
                        }

                        mContactinfoDao.sort((int) l);

                        mMydbHeper = new MydbHeper(view1.getContext());
                        sqLiteDatabase = mMydbHeper.getReadableDatabase();
                        Cursor cursor = sqLiteDatabase.query("contactinfo",null,null,null,null,null,null);
                        String[] from = {"_id","name","laser"};
                        int[] to = {R.id.textview1,R.id.textview2,R.id.textview3};
                        adapter = new SimpleCursorAdapter(view1.getContext(),R.layout.fragment_database,cursor,from,to);
                        listView.setAdapter(adapter);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
                return true;
            }
        });

        // 点击显示光谱曲线
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {     //单击显示光谱曲线
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mContactinfoDao = new ContactinfoDao(view.getContext());
                String backSpuctrum = mContactinfoDao.alertData((int) l);
                callBackValue.SendMessageValue(backSpuctrum);
                //MainActivity中
            }
        });
        return view;
    }

    public interface CallBackValue{
        public void SendMessageValue(String strValue);
    }

}
