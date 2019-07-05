package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.util.Date;
import java.util.zip.DataFormatException;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private int privority=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);
        CheckboxListen();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void CheckboxListen() {
        RadioGroup radioGroup = findViewById(R.id.radiogroup);
        RadioButton radioButton1=findViewById(R.id.radioButton1);
        RadioButton radioButton2=findViewById(R.id.radioButton2);
        RadioButton radioButton3=findViewById(R.id.radioButton3);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton1:{
                        privority=1;
                        break;
                    }
                    case R.id.radioButton2:{
                        privority=2;
                        break;
                    }
                    case R.id.radioButton3:{
                        privority=3;
                        break;
                    }
                    default:
                        break;
                }
            }
        });

    }


    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        TodoDbHelper todoDbHelper = new TodoDbHelper(this);


        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(TodoContract.COLUMN_NAME_Content,content);
            contentValues.put(TodoContract.COLUMN_NAME_DATE,System.currentTimeMillis());

            contentValues.put(TodoContract.COLUMN_NAME_STATE,0);
            contentValues.put(TodoContract.COLUMN_NAME_PRIVORITY,this.privority);

            long newRowId =db.insert(TodoContract.TABLE_NAME,null,contentValues);
            this.privority =0;

            Log.d("2", "saveNote2Database: "+newRowId);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            todoDbHelper.close();
            return true;
        }

    }
}
