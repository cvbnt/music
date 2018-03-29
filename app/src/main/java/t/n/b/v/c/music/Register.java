package t.n.b.v.c.music;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 54654 on 2018/3/17.
 */

public class Register extends AppCompatActivity {
    private SqliteHelper dbHelper;
    private EditText RegisterUserEdit;
    private EditText RegisterPwdEdit;
    private Button register_Button;
    private Button cancelButton;
    private static int CheckCode=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        dbHelper = new SqliteHelper(this);
        fvbi();
        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String getUser = RegisterUserEdit.getText().toString().trim();
                String getPwd = RegisterPwdEdit.getText().toString().trim();
                if ((getUser.length() == 0) && (getPwd.length() != 0)) {
                    Toast.makeText(Register.this, R.string.please_input_username, Toast.LENGTH_SHORT).show();
                } else if ((getPwd.length() == 0) && (getUser.length() != 0)) {
                    Toast.makeText(Register.this, R.string.please_input_password, Toast.LENGTH_SHORT).show();
                } else if ((getUser.length() != 0) && (getPwd.length() != 0)) {
                    Cursor cursor = db.query("data", null, null, null, null, null, null);
                    CheckRepeat(getUser, cursor);
                    if (CheckCode == 1) {
                        SignUp(db, getUser, getPwd);
                    }
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
    private void fvbi() {
        RegisterUserEdit = (EditText) findViewById(R.id.user_enter_register);
        RegisterPwdEdit = (EditText) findViewById(R.id.pwd_enter_register);
        register_Button = (Button) findViewById(R.id.register_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
    }
    private void CheckRepeat(String getUser, Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex("username"));
                if (getUser.equals(username)) {
                    Toast.makeText(Register.this, R.string.user_name_has_been_registered, Toast.LENGTH_SHORT).show();
                    RegisterUserEdit.setText("");
                    CheckCode = 0;
                    break;
                } else {
                    CheckCode = 1;
                    break;
                }
            } while (cursor.moveToFirst());
        }
        cursor.close();
    }
    private void SignUp(SQLiteDatabase db, String getUser, String getPwd) {
        ContentValues values = new ContentValues();
        values.put("username", getUser);
        values.put("password", getPwd);
        db.insert("data", null, values);
        Toast.makeText(Register.this, R.string.successfully_created_an_account, Toast.LENGTH_SHORT).show();
    }
}