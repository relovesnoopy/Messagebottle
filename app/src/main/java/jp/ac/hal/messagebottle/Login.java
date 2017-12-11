package jp.ac.hal.messagebottle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nifty.cloud.mb.core.LoginCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;

public class Login extends AppCompatActivity {
    private EditText nametext;
    private EditText passwordtext;
    private TextView tv;
    private Button loginbtn;
    private Button signupbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginbtn = (Button)findViewById(R.id.btn_login);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        signupbtn = (Button) findViewById(R.id.link_signup);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }


    private void login() {

        //ログインエラー
        if (!logincheck()) {
            onLoginFailed();
            return;
        }

        loginbtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("接続中...");
        progressDialog.show();

        nametext = (EditText)findViewById(R.id.input_name);
        passwordtext = (EditText)findViewById(R.id.input_password);

        String name = nametext.getText().toString();
        String password = passwordtext.getText().toString();

        // TODO: Implement your own authentication logic here.
        //ユーザ名とパスワードを指定してログインを実行
        try {
            NCMBUser.loginInBackground(name, password, new LoginCallback() {
                @Override
                public void done(NCMBUser user, NCMBException e) {
                    if (e != null) {
                        //エラー時の処理
                        onLoginFailed();
                    } else {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onLoginSuccess or onLoginFailed
                                        onLoginSuccess();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                }
            });
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        super.onBackPressed();
        Toast.makeText(getBaseContext(), "back", Toast.LENGTH_LONG).show();
        finish();
        moveTaskToBack(true);

    }

    public void onLoginSuccess() {
        loginbtn.setEnabled(true);
        //ログインフラグを立てる
        MainActivity.loginflg = true;
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "ログインに失敗しました", Toast.LENGTH_LONG).show();

        loginbtn.setEnabled(true);
    }

    public boolean logincheck() {
        boolean valid = true;

        nametext = (EditText)findViewById(R.id.input_name);
        passwordtext = (EditText)findViewById(R.id.input_password);

        String name = nametext.getText().toString();
        String password = passwordtext.getText().toString();

        if (name.isEmpty()) {
            nametext.setError("ユーザ名を入力してください");
            valid = false;
        } else {
            nametext.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordtext.setError("4〜10文字の英数字を入力してください");
            valid = false;
        } else {
            passwordtext.setError(null);
        }

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();

    }
}
