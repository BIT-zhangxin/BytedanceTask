package com.byted.camp.todolist;

import static com.byted.camp.todolist.R.string.*;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.byted.camp.todolist.R.string;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;

    RadioGroup radioGroup;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        radioGroup=findViewById(R.id.radioGroup);

        initDatabase();
    }

    @Override
    protected void onDestroy() {
        db.close();
        dbHelper.close();
        super.onDestroy();
    }

    private void initDatabase(){
        dbHelper=new TodoDbHelper(this);
        db=dbHelper.getWritableDatabase();
    }

    private boolean saveNote2Database(String content) {

        if(db==null){
            return false;
        }

        ContentValues values=new ContentValues();
        values.put(TodoContract.date,new Date().getTime());
        values.put(TodoContract.state, 0);
        values.put(TodoContract.content,content);

        int radioGroupId=radioGroup.getCheckedRadioButtonId();
        String radioButtonName=((RadioButton)findViewById(radioGroupId)).getText().toString();
        int priority;
        switch (radioButtonName){
            case "一般事项":
                priority=1;
                break;
            case "重要事项":
                priority=2;
                break;
            case "紧急事项":
                priority=3;
                break;
            default:
                priority=1;
                break;
        }
        values.put(TodoContract.priority,priority);

        try {
            db.insert(TodoContract.TABLE_NAME,null,values);
        }catch (SQLiteException e){
            e.printStackTrace();
            return false;
        }
        return true;

        // TODO 插入一条新数据，返回是否插入成功
    }
}
