package jp.ac.hal.messagebottle

import android.app.Activity
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.nifty.cloud.mb.core.DoneCallback
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBUser

class SignupActivity : AppCompatActivity() {
    private var signupbtn: Button? = null
    private var nametext: EditText? = null
    private var passwordtext: EditText? = null

    //internal val progressDialog = ProgressDialog(this@SignupActivity, R.style.Theme_AppCompat_DayNight_Dialog)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupbtn = findViewById(R.id.btn_signup) as Button
        signupbtn!!.setOnClickListener { signup() }
    }

    private fun signup() {
        if (!signupcheck()) {
            onSignupFailed()
            return
        }

        signupbtn!!.isEnabled = false

        //final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        val progressDialog = ProgressDialog(this,  R.style.Theme_AppCompat_DayNight_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("アカウントを作成しています...")
        progressDialog.show()

        nametext = findViewById(R.id.input_name) as EditText
        passwordtext = findViewById(R.id.input_password) as EditText

        val name = nametext!!.text.toString()
        val password = passwordtext!!.text.toString()


        //NCMBUserのインスタンスを作成
        val user = NCMBUser()
        //ユーザ名を設定
        user.userName = name
        //パスワードを設定
        user.setPassword(password)
        //設定したユーザ名とパスワードで会員登録を行う
        user.signUpInBackground { e ->
            if (e != null) {
                //会員登録時にエラーが発生した場合の処理
                onSignupFailed()
            } else {
                //３秒後に遷移する
                android.os.Handler().postDelayed(
                        {
                            onSignupSuccess()
                            progressDialog.dismiss()
                        }, 3000)
            }
        }
    }

    fun onSignupSuccess() {
        val progressDialog = ProgressDialog(this,  R.style.Theme_AppCompat_DayNight_Dialog)
        signupbtn!!.isEnabled = true
        setResult(Activity.RESULT_OK, null)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("アカウントを作成しました。")
        progressDialog.show()
        //Login画面へ戻る
        finish()
    }

    fun onSignupFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_SHORT).show()
        signupbtn!!.isEnabled = true
    }

    fun signupcheck(): Boolean {
        var valid = true
        nametext = findViewById(R.id.input_name) as EditText
        passwordtext = findViewById(R.id.input_password) as EditText

        val name = nametext!!.text.toString()
        val password = passwordtext!!.text.toString()

        //ユーザ名の入力チェック
        if (name.isEmpty() || name.length < 3) {
            nametext!!.error = "ユーザ名は3文字以上です"
            valid = false
        } else {
            passwordtext!!.error = null
        }

        //パスワードの入力チェック
        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordtext!!.error = "4〜10文字の英数字を入力してください"
            valid = false
        } else {
            passwordtext!!.error = null
        }

        return valid
    }

}
