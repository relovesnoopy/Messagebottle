package jp.ac.hal.messagebottle

import android.app.FragmentManager
import android.net.Uri
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.RelativeLayout

/**
 *
 */
class MainContentsActivity : AppCompatActivity(), MainFragment.MainFragmentLisner, ChildMainFragment.ChildMainFragmentListener, MainFragment.OnFragmentInteractionListener {
    private var behavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_contents)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //java:getSupportActionBar() kt:supportActionBar!!
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val bottomSheet = findViewById(R.id.bottom_sheet) as RelativeLayout
        behavior = BottomSheetBehavior.from(bottomSheet)
        //getFragmentManager()
        val fragmentManager :FragmentManager = fragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.fragment_parent, MainFragment.newInstance("p1", "p2"))
        fragmentTransaction.commit()
    }

    override fun OnShowChild(filepath: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.child_fragment, ChildMainFragment.newInstance(filepath, "filepath"))
        ft.commit()
        behavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCloseChildMain() {
        behavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
