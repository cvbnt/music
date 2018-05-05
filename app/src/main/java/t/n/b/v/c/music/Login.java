package t.n.b.v.c.music;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

/**
 * Created by 54654 on 2018/3/17.
 */
public class Login extends AppCompatActivity {
    private static int LOGGED=0;                                    //登录状态码，用以识别是否勾选用户名密码
    private static final int REQUEST_STORAGE_PERMISSIONS=0;         //申请内存读写权限请求码
    private static final String[] STORAGE_PERMISSIONS=new String[]{ //申请的权限名称字符串
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private SqliteHelper dbhelpder;
    private EditText userEdit;
    private EditText pwdEdit;
    private Button loginButton;
    private Button registerButton;
    private CheckBox mCheckBox;
    private static int loginCode;                                   //登录状态码，验证密码正确与否状态
    private android.support.v7.widget.Toolbar mLoginToolBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        checkLogged();                                          //检查是否勾选记住用户名密码
        fvbi();                                                 //绑定布局控件ID
        setSupportActionBar(mLoginToolBar);                     //设置ToolBar
        dbhelpder=new SqliteHelper(this);              //新建数据库对象
        registerButton.setOnClickListener(view -> {             //注册按钮点击事件
            Intent intent=new Intent(Login.this,Register.class);
            startActivity(intent);                              //跳转到注册所在的Activity
            dbhelpder.getWritableDatabase();                    //数据库写入
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  //设置checkBox勾选事件
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){                                          //如果勾选
                    LOGGED=1;                                    //LOGGED状态码为1
                }else {                                          //不勾选
                    LOGGED=0;                                    //状态码继续为0
                }
            }
        });
        loginButton.setOnClickListener(view -> {                //登录按钮点击事件
            SQLiteDatabase db=dbhelpder.getWritableDatabase();  //数据库写入操作
            String loginUser=userEdit.getText().toString().trim(); //获取登录框字符
            String loginPwd=pwdEdit.getText().toString().trim();   //获取密码框字符
            if ((loginUser.length() == 0) && (loginPwd.length() != 0)) { //如果登录框为空而密码框不为空
                Toast.makeText(Login.this, R.string.please_input_username, Toast.LENGTH_SHORT).show(); //显示提示消息"请输入用户名"
            } else if ((loginPwd.length() == 0) && (loginUser.length() != 0)) { //如果登录框不为空而密码框为空
                Toast.makeText(Login.this, R.string.please_input_password, Toast.LENGTH_SHORT).show(); //显示提示消息"请输入密码"
            } else if ((loginUser.length() != 0) && (loginPwd.length() != 0)) { //如果登录框不为空且密码框不为空
                Cursor cursor=db.query("data",null,null,null,null,null,null); //查询本地数据库表"table"
                if (cursor.moveToFirst()) {  //cursor循环查询
                    do {
                        String username = cursor.getString(cursor.getColumnIndex("username"));
                        String password=cursor.getString(cursor.getColumnIndex("password"));
                        if (loginUser.equals(username)) {   //如果用户名和数据库内已存在的用户名相同，则往下比对密码
                            if (loginPwd.equals(password)){ //如果密码也相同
                                loginCode=1;                //loginCode状态码为1
                                break;
                            }else{
                                loginCode=0;                //密码不符则为0
                                break;
                            }
                        } else {
                            loginCode=0;                    //数据库没找到相同用户名也为0
                            break;
                        }
                    } while (cursor.moveToFirst());
                }
                cursor.close(); //关闭cursor
                if (loginCode == 1) {            //状态码为1情况下，账号密码都正确
                        if (checkPermissions()){ //检查权限，如果得到权限
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            checkCheck();        //如果checkBox勾选
                            startActivity(intent);
                        }else {
                            requestPermissions(STORAGE_PERMISSIONS,REQUEST_STORAGE_PERMISSIONS); //没得到权限则申请权限
                        }
                } else {
                    Toast.makeText(Login.this, R.string.wrong_user_pwd, Toast.LENGTH_SHORT).show(); //错误用户名密码提示
                    userEdit.setText("");  //输入框清空
                    pwdEdit.setText("");   //密码框清空
                }
            }else{
                Toast.makeText(Login.this, R.string.user_pwd_empty,Toast.LENGTH_SHORT).show(); //最后一种情况是两个输入框全为空
            }
        });
    }

    private void checkCheck() {
        SharedPreferences.Editor editor=getSharedPreferences("logged",MODE_PRIVATE).edit(); //读写本地logged文件
        editor.putInt("LOGGED",LOGGED);                   //写入LOGGED这个状态码
        editor.apply();                                      //请求接受
    }

    private void checkLogged() {
        SharedPreferences pref=getSharedPreferences("logged",MODE_PRIVATE);  //读写本地logged文件
        int logged=pref.getInt("LOGGED",0);                                   //获取文件内的LOGGED值
        if (logged!=LOGGED){                                                       //如果文件内值为1
            Intent intent=new Intent(Login.this,MainActivity.class); //勾选过，直接跳转到主界面
            startActivity(intent);
        }
    }

    private void fvbi() {
        userEdit=(EditText)findViewById(R.id.user_enter_register);
        pwdEdit=(EditText) findViewById(R.id.pwd_enter_register);
        loginButton=(Button)findViewById(R.id.login_button);
        registerButton=(Button) findViewById(R.id.register_an_account_button);
        mCheckBox=(CheckBox)findViewById(R.id.checkBox);
        mLoginToolBar=findViewById(R.id.loginBar);
    }
    private boolean checkPermissions(){               //检查权限
        int result=ContextCompat.checkSelfPermission(Login.this,STORAGE_PERMISSIONS[0]); //result为是否拥有了权限字符串内的权限结果
        return result==PackageManager.PERMISSION_GRANTED;             //如果resulth和PackageManager.PERMISSION_GRANTED值相同，则已拥有权限
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { //申请权限
        switch (requestCode){                             //申请码
            case REQUEST_STORAGE_PERMISSIONS:
                if(checkPermissions()){                   //如果申请了权限
                    Intent intent = new Intent(Login.this, MainActivity.class); //跳转主界面
                    checkCheck();                         //检测是否勾选用户名密码
                    startActivity(intent);
                }
                default:
                    super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
