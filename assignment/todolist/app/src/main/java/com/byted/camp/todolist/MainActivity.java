package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.security.KeyStore;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

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
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });

        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
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

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        TodoDbHelper todoDbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = todoDbHelper.getReadableDatabase();

        if(db == null){
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TodoContract.TABLE_NAME,
                    new String[]{
                            TodoContract.COLUMN_NAME_ID,
                            TodoContract.COLUMN_NAME_DATE,
                            TodoContract.COLUMN_NAME_Content,
                            TodoContract.COLUMN_NAME_STATE,
                            TodoContract.COLUMN_NAME_PRIVORITY
                            },
                    null,null,
                    null,null,
                    TodoContract.COLUMN_NAME_PRIVORITY +" DESC, "+
                        TodoContract.COLUMN_NAME_DATE+" DESC ");
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(TodoContract.COLUMN_NAME_ID));
                String content = cursor.getString(cursor.getColumnIndex(
                        TodoContract.COLUMN_NAME_Content));
                long date = cursor.getLong(cursor.getColumnIndex(
                        TodoContract.COLUMN_NAME_DATE));
                int state = cursor.getInt(cursor.getColumnIndex(TodoContract.COLUMN_NAME_STATE));
                int privority = cursor.getInt(cursor.getColumnIndex(TodoContract.COLUMN_NAME_PRIVORITY));

                Note note = new Note(id);
                note.setContent(content);
                note.setDate(new Date(date));
                note.setState(State.from(state));
                note.setPrivority(privority);

                result.add(note);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
            todoDbHelper.close();
        }

        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        TodoDbHelper todoDbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        try{
            String selection = TodoContract.COLUMN_NAME_ID+ " LIKE ? ";
            String[] selectionArgs = { ""+note.getId()};
            int deleteRows = db.delete(TodoContract.TABLE_NAME,selection,selectionArgs);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            todoDbHelper.close();
        }

        notesAdapter.refresh(loadNotesFromDatabase());

    }

    private void updateNode(Note note) {
        // 更新数据

        TodoDbHelper todoDbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        try{

            ContentValues values = new ContentValues();
            long change_state=0;
            if(note.getState() == State.DONE){
                values.put(TodoContract.COLUMN_NAME_STATE,1);
            }
            else{
                values.put(TodoContract.COLUMN_NAME_STATE,0);
            }
            String selection =TodoContract.COLUMN_NAME_ID+" LIKE ? ";
            String[] selectionArgs = { ""+note.getId()};
            int count = db.update(TodoContract.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            Log.d("dsf", "updateNode: "+count);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            todoDbHelper.close();
        }

        notesAdapter.refresh(loadNotesFromDatabase());


    }

}
