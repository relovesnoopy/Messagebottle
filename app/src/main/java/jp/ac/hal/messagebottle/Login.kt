package jp.ac.hal.messagebottle

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.nifty.cloud.mb.core.LoginCallback
import com.nifty.cloud.mb.core.NCMBException
import com.nifty.cloud.mb.core.NCMBUser

class Login : AppCompatActivity() {
    private var nametext: EditText? = null
    private var passwordtext: EditText? = null
    private val tv: TextView? = null
    private var loginbtn: Button? = null
    private var signupbtn: Button? = null
    private var user: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val tool = findViewById(R.id.LoginToolBar) as Toolbar
        setSupportActionBar(tool)
        loginbtn = findViewById(R.id.btn_login) as Button
        loginbtn?.setOnClickListener { login() }
        signupbtn = findViewById(R.id.link_signup) as Button
        signupbtn?.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivityForResult(intent, 0)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

    }

    private fun login() {

        //ログインエラー
        if (!logincheck()) {
            onLoginFailed()
            return
        }

        loginbtn!!.isEnabled = false

        val progressDialog = ProgressDialog(this@Login, R.style.Theme_AppCompat_DayNight_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("接続中...")
        progressDialog.show()

        nametext = findViewById(R.id.input_name) as EditText
        passwordtext = findViewById(R.id.input_password) as EditText

        user = nametext!!.text.toString()
        val password = passwordtext!!.text.toString()

        // TODO: Implement your own authentication logic here.
        //ユーザ名とパスワードを指定してログインを実行
        try {
            NCMBUser.loginInBackground(user, password) { user, e ->
                if (e != null) {
                    //エラー時の処理
                    onLoginFailed()
                } else {
                    android.os.Handler().postDelayed(
                            {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess()
                                progressDialog.dismiss()
                            }, 3000)
                }
            }
        } catch (e: NCMBException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish()
            }
        }
    }

    override fun onBackPressed() {
        // Disable going back to the MainActivity
        super.onBackPressed()
        //Toast.makeText(getBaseContext(), "back", Toast.LENGTH_LONG).show();
        finish()
        //moveTaskToBack(true);

    }

    fun onLoginSuccess() {
        loginbtn!!.isEnabled = true
        //ログインフラグを立てる
        MainActivity.user_name = user ?: "TestUser"
        MainActivity.loginflg = true
        finish()
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "ログインに失敗しました", Toast.LENGTH_LONG).show()

        loginbtn!!.isEnabled = true
    }

    fun logincheck(): Boolean {
        var valid = true

        nametext = findViewById(R.id.input_name) as EditText
        passwordtext = findViewById(R.id.input_password) as EditText

        val name = nametext?.text.toString()
        val password = passwordtext?.text.toString()

        if (name.isEmpty()) {
            nametext?.error = "ユーザ名を入力してください"
            valid = false
        } else {
            nametext?.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordtext?.error = "4〜10文字の英数字を入力してください"
            valid = false
        } else {
            passwordtext?.error = null
        }

        return valid
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        var result = true
        when (id){
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()

    }
}
