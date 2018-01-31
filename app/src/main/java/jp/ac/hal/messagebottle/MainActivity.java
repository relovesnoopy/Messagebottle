package jp.ac.hal.messagebottle;


import com.nifty.cloud.mb.core.NCMB;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener{
    public static String user_name = "testUser";
    public static boolean loginflg;
    //画面サイズ
    public static int view_width;
    public static int view_height;
    private boolean networkflg ;

    private static Context sContext;

    private BottomSheetBehavior behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルバーを消す
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        sContext = this;
        //file:///storage/emulated/0/Pictures/IMG/1513181867796.jpg


        //端末のサイズ取得
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        //サイズ設定
        view_width = size.x;
        view_height = size.y;

        //APIキーの設定とSDKの初期化
        NCMB.initialize(this.getApplicationContext(), "65515bc5e2bc943adba7f1d767cb0d4b6dbf823db2cab262b5d90a4cdd551346", "a46f230a9661a8d0c24dca36d2aaf6ea47fb97f30c72fb316095207baca4e05b");

        if (NetworkManager.INSTANCE.isConnected(this)) {
            networkflg = true;
        } else {
            networkflg = false;
        }
        if(networkflg) {

            setContentView(R.layout.activity_main);
            TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
            tabs.addTab(tabs.newTab());
            tabs.addTab(tabs.newTab());
            tabs.addTab(tabs.newTab());
            // ViewPager
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            // 各コンテンツとなるフラグメントをセットするアダプターをViewPagerにセット
            // Fragmentを操作するためにコンストラクタの引数にFragmentManagerを渡しスーパークラスにセット。
            MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

            // アダプターに各ページ要素となるフラグメントを追加
            pagerAdapter.addFragment(CameraFragment.newInstance("camera", "カメラ"));
            //pagerAdapter.addFragment(CameraFragment.newInstance("main", "メッセージ一覧"));
            pagerAdapter.addFragment(Userfragment.newInstance("setting", "設定"));

            // ViewPagerにアダプタをセット
            viewPager.setAdapter(pagerAdapter);

            // TabLayoutとViewPagerをバインド
            tabs.setupWithViewPager(viewPager);
        }
        //netに接続されていなかったら
        else {
            /*Intent intent = new Intent(this, Login.class);
            startActivity(intent);*/
            setContentView(R.layout.top_layout);
            ImageView topimage = (ImageView)findViewById(R.id.topimage);
            topimage.setOnClickListener(v -> loginflg = true);
        }
    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static Context getContext() {
        //このアプリのコンテキストを返す
        return sContext;
    }


}
