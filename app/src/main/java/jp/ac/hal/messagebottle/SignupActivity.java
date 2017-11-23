package jp.ac.hal.messagebottle;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;

public class SignupActivity extends AppCompatActivity {
    private Button signupbtn;
    private EditText nametext;
    private EditText passwordtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupbtn = (Button)findViewById(R.id.btn_signup);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup() {
        if (!signupcheck()) {
            onSignupFailed();
            return;
        }

        signupbtn.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("アカウントを作成しています...");
        progressDialog.show();

        nametext = (EditText)findViewById(R.id.input_name);
        passwordtext = (EditText)findViewById(R.id.input_password);

        String name = nametext.getText().toString();
        String password = passwordtext.getText().toString();


        //NCMBUserのインスタンスを作成
        NCMBUser user = new NCMBUser();
        //ユーザ名を設定
        user.setUserName(name);
        //パスワードを設定
        user.setPassword(password);
        //設定したユーザ名とパスワードで会員登録を行う
        user.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    //会員登録時にエラーが発生した場合の処理
                    onSignupFailed();
                } else {
                    //３秒後に遷移する
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    onSignupSuccess();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });
    }

    public void onSignupSuccess() {
        signupbtn.setEnabled(true);
        setResult(RESULT_OK, null);
        //Login画面へ戻る
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();

        signupbtn.setEnabled(true);
    }

    public boolean signupcheck() {
        boolean valid = true;
        nametext = (EditText)findViewById(R.id.input_name);
        passwordtext = (EditText)findViewById(R.id.input_password);

        String name = nametext.getText().toString();
        String password = passwordtext.getText().toString();

        //ユーザ名の入力チェック
        if (name.isEmpty() || name.length() < 3) {
            nametext.setError("ユーザ名は3文字以上です");
            valid = false;
        } else {
            passwordtext.setError(null);
        }

        //パスワードの入力チェック
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordtext.setError("4〜10文字の英数字を入力してください");
            valid = false;
        } else {
            passwordtext.setError(null);
        }

        return valid;
    }

}
