package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(notesAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                //监控上下左右
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = 0;//ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT

                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                notesAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                //item移动后，也要移动数据
                Collections.swap(notesAdapter.get(),viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //左右拖动的回调，不处理
            }

            @Override
            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                //开启后，当前item被拖动到其他位置后,其间的item会坠落或上浮
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否开启长按拖动
                return true;
            }
        };

        //创建item helper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

        //绑定到recyclerView上面
        itemTouchHelper.attachToRecyclerView(recyclerView);

        initDatabase();

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void initDatabase(){
        dbHelper=new TodoDbHelper(this);
        db=dbHelper.getWritableDatabase();
    }

    private List<Note> loadNotesFromDatabase() {

        if(db==null){
            return Collections.emptyList();
        }

        List<Note> list=new ArrayList<>();
        Cursor cursor=null;
        String[] args={TodoContract.id,TodoContract.date,TodoContract.state,TodoContract.content};
        try {
            cursor=db.query(
                TodoContract.TABLE_NAME,
                args,null,
                null,
                null,
                null,
                TodoContract.date+" DESC");
            while (cursor.moveToNext()){

                Note note=new Note(cursor.getLong(cursor.getColumnIndex(TodoContract.id)));
                note.setDate(new Date(cursor.getLong(cursor.getColumnIndex(TodoContract.date))));
                note.setState(State.from(cursor.getInt(cursor.getColumnIndex(TodoContract.state))));
                note.setContent(cursor.getString(cursor.getColumnIndex(TodoContract.content)));
                list.add(note);
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        // TODO 从数据库中查询数据，并转换成 JavaBeans
        return list;
    }

    private void deleteNote(Note note) {

        if(db==null){
            return;
        }

        String[] args={String.valueOf(note.id)};

        try {
            db.delete(TodoContract.TABLE_NAME,TodoContract.WHERE_CLAUSE,args);
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        notesAdapter.delete(note);

        // TODO 删除数据
    }

    private void updateNode(Note note) {

        if(db==null){
            return;
        }

        String[] args={String.valueOf(note.id)};

        ContentValues values=new ContentValues();
        values.put(TodoContract.date,note.getDate().getTime());
        values.put(TodoContract.state, note.getState().intValue);
        values.put(TodoContract.content,note.getContent());

        try {

            db.update(TodoContract.TABLE_NAME,values,TodoContract.WHERE_CLAUSE,args);

        }catch (SQLiteException e){
            e.printStackTrace();
        }

        // TODO 更新数据
    }

}
