package jp.ac.hal.messagebottle


import com.nifty.cloud.mb.core.NCMB
import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout


class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener {
    private var networkflg: Boolean = false

    private val behavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // タイトルバーを消す
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this
        //file:///storage/emulated/0/Pictures/IMG/1513181867796.jpg


        //端末のサイズ取得
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val disp = wm.defaultDisplay
        val size = Point()
        disp.getSize(size)
        //サイズ設定
        view_width = size.x
        view_height = size.y

        //APIキーの設定とSDKの初期化
        NCMB.initialize(this.applicationContext, "65515bc5e2bc943adba7f1d767cb0d4b6dbf823db2cab262b5d90a4cdd551346", "a46f230a9661a8d0c24dca36d2aaf6ea47fb97f30c72fb316095207baca4e05b")

        networkflg = NetworkManager.isConnected(this)
        if (networkflg) {

            setContentView(R.layout.activity_main)
            val tabs = findViewById(R.id.tabs) as TabLayout
            tabs.addTab(tabs.newTab())
            tabs.addTab(tabs.newTab())
            tabs.addTab(tabs.newTab())
            // ViewPager
            val viewPager = findViewById(R.id.pager) as ViewPager
            // 各コンテンツとなるフラグメントをセットするアダプターをViewPagerにセット
            // Fragmentを操作するためにコンストラクタの引数にFragmentManagerを渡しスーパークラスにセット。
            val pagerAdapter = MyFragmentPagerAdapter(supportFragmentManager)

            // アダプターに各ページ要素となるフラグメントを追加
            pagerAdapter.addFragment(CameraFragment.newInstance("camera", "カメラ"))
            //pagerAdapter.addFragment(CameraFragment.newInstance("main", "メッセージ一覧"));
            pagerAdapter.addFragment(Userfragment.newInstance("setting", "設定"))
            // ViewPagerにアダプタをセット
            viewPager.adapter = pagerAdapter
            // TabLayoutとViewPagerをバインド
            tabs.setupWithViewPager(viewPager)

        } else {
            setContentView(R.layout.top_layout)
            val topimage = findViewById(R.id.topimage) as ImageView
            topimage.setOnClickListener { loginflg = true }
        }//netに接続されていなかったら
    }


    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    companion object {
        var user_name = "testUser"
        var loginflg: Boolean = false
        //画面サイズ
        var view_width: Int = 0
        var view_height: Int = 0

        //このアプリのコンテキストを返す
        var context: Context? = null
            private set
    }


}
