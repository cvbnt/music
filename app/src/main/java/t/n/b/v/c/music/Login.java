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

/**
 * Created by 54654 on 2018/3/17.
 */
public class Login extends AppCompatActivity {
    private static int LOGGED=0;
    private static final int REQUEST_STORAGE_PERMISSIONS=0;
    private static final String[] STORAGE_PERMISSIONS=new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private SqliteHelper dbhelpder;
    private EditText userEdit;
    private EditText pwdEdit;
    private Button loginButton;
    private Button registerButton;
    private CheckBox mCheckBox;
    private static int loginCode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        checkLogged();
        fvbi();
        dbhelpder=new SqliteHelper(this);
        registerButton.setOnClickListener(view -> {
            Intent intent=new Intent(Login.this,Register.class);
            startActivity(intent);
            dbhelpder.getWritableDatabase();
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    LOGGED=1;
                }else {
                    LOGGED=0;
                }
            }
        });
        loginButton.setOnClickListener(view -> {
            SQLiteDatabase db=dbhelpder.getWritableDatabase();
            String loginUser=userEdit.getText().toString().trim();
            String loginPwd=pwdEdit.getText().toString().trim();
            if ((loginUser.length() == 0) && (loginPwd.length() != 0)) {
                Toast.makeText(Login.this, R.string.please_input_username, Toast.LENGTH_SHORT).show();
            } else if ((loginPwd.length() == 0) && (loginUser.length() != 0)) {
                Toast.makeText(Login.this, R.string.please_input_password, Toast.LENGTH_SHORT).show();
            } else if ((loginUser.length() != 0) && (loginPwd.length() != 0)) {
                Cursor cursor=db.query("data",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username = cursor.getString(cursor.getColumnIndex("username"));
                        String password=cursor.getString(cursor.getColumnIndex("password"));
                        if (loginUser.equals(username)) {
                            if (loginPwd.equals(password)){
                                loginCode=1;
                                break;
                            }else{
                                loginCode=0;
                                break;
                            }
                        } else {
                            loginCode=0;
                            break;
                        }
                    } while (cursor.moveToFirst());
                }
                cursor.close();
                if (loginCode == 1) {
                        if (checkPermissions()){
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            checkCheck();
                            startActivity(intent);
                        }else {
                            requestPermissions(STORAGE_PERMISSIONS,REQUEST_STORAGE_PERMISSIONS);
                        }
                } else {
                    Toast.makeText(Login.this, R.string.wrong_user_pwd, Toast.LENGTH_SHORT).show();
                    userEdit.setText("");
                    pwdEdit.setText("");
                }
            }else{
                Toast.makeText(Login.this, R.string.user_pwd_empty,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCheck() {
        SharedPreferences.Editor editor=getSharedPreferences("logged",MODE_PRIVATE).edit();
        editor.putInt("LOGGED",LOGGED);
        editor.apply();
    }

    private void checkLogged() {
        SharedPreferences pref=getSharedPreferences("logged",MODE_PRIVATE);
        int logged=pref.getInt("LOGGED",0);
        if (logged!=LOGGED){
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
        }
    }

    private void fvbi() {
        userEdit=(EditText)findViewById(R.id.user_enter_register);
        pwdEdit=(EditText) findViewById(R.id.pwd_enter_register);
        loginButton=(Button)findViewById(R.id.login_button);
        registerButton=(Button) findViewById(R.id.register_an_account_button);
        mCheckBox=(CheckBox)findViewById(R.id.checkBox);
    }
    private boolean checkPermissions(){
        int result=ContextCompat.checkSelfPermission(Login.this,STORAGE_PERMISSIONS[0]);
        return result==PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_STORAGE_PERMISSIONS:
                if(checkPermissions()){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    checkCheck();
                    startActivity(intent);
                }
                default:
                    super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
