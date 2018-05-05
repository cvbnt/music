package t.n.b.v.c.music;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private static int CheckCode=1;     //检测状态码
    private Toolbar mRegisterBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        dbHelper = new SqliteHelper(this);
        fvbi();  //绑定控件ID
        setSupportActionBar(mRegisterBar);  //设置ToolBar
        register_Button.setOnClickListener(new View.OnClickListener() {  //点击注册按钮时间
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();    //写入数据库
                String getUser = RegisterUserEdit.getText().toString().trim();  //获取用户名输入框字符
                String getPwd = RegisterPwdEdit.getText().toString().trim();    //获取密码输入框字符
                if ((getUser.length() == 0) && (getPwd.length() != 0)) {        //如果用户名输入框为空密码框不为空
                    Toast.makeText(Register.this, R.string.please_input_username, Toast.LENGTH_SHORT).show(); //提示"请输入用户名"
                } else if ((getPwd.length() == 0) && (getUser.length() != 0)) { //如果用户名输入框不为空密码框为空
                    Toast.makeText(Register.this, R.string.please_input_password, Toast.LENGTH_SHORT).show(); //提示"请输入密码"
                } else if ((getUser.length() != 0) && (getPwd.length() != 0)) { //如果用户名输入框和密码输入框都不为空
                    Cursor cursor = db.query("data", null, null, null, null, null, null); //查询"data"表
                    CheckRepeat(getUser, cursor);   //查看是否存在该用户名
                    if (CheckCode == 1) {           //状态码为1
                        SignUp(db, getUser, getPwd);//注册
                    }
                }else {
                    Toast.makeText(Register.this,"用户名和密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {  //点击取消按钮返回Login的Activity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
    private void fvbi() {
        mRegisterBar=findViewById(R.id.registerBar);
        RegisterUserEdit = (EditText) findViewById(R.id.user_enter_register);
        RegisterPwdEdit = (EditText) findViewById(R.id.pwd_enter_register);
        register_Button = (Button) findViewById(R.id.register_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
    }
    private void CheckRepeat(String getUser, Cursor cursor) {
        if (cursor.moveToFirst()) {    //指针循环查询
            do {
                String username = cursor.getString(cursor.getColumnIndex("username"));
                if (getUser.equals(username)) {   //如果查询到重复用户名
                    Toast.makeText(Register.this, R.string.user_name_has_been_registered, Toast.LENGTH_SHORT).show(); //提示"用户名已被注册，请输入其他用户名"
                    RegisterUserEdit.setText("");          //清空用户名输入框
                    CheckCode = 0;                         //该状态码值为0
                    break;
                } else {
                    CheckCode = 1;                         //没被注册过该账号，则状态码值为1
                    break;
                }
            } while (cursor.moveToFirst());
        }
        cursor.close();
    }
    private void SignUp(SQLiteDatabase db, String getUser, String getPwd) {
        ContentValues values = new ContentValues();      //values写入
        values.put("username", getUser);                 //用户名
        values.put("password", getPwd);                  //密码
        db.insert("data", null, values);  //数据库插入数据
        Toast.makeText(Register.this, R.string.successfully_created_an_account, Toast.LENGTH_SHORT).show();  //提示信息"成功创建账号"
    }
}