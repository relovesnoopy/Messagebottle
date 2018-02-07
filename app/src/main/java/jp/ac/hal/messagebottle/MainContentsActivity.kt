package jp.ac.hal.messagebottle

import android.app.FragmentManager
import android.net.Uri
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.nifty.cloud.mb.core.NCMBObject

/**
 *
 */
class MainContentsActivity : AppCompatActivity(), MainFragment.MainFragmentLisner, ChildMainFragment.ChildMainFragmentListener, MainFragment.OnFragmentInteractionListener{
    private var behavior: BottomSheetBehavior<*>? = null
    private var fileEntity: FileEntity? = null
    private var MESSAGECODE:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_contents)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        MESSAGECODE = intent.getIntExtra("MESSAGE", 0)
        //java:getSupportActionBar() kt:supportActionBar!!
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val bottomSheet = findViewById(R.id.bottom_sheet) as RelativeLayout
        behavior = BottomSheetBehavior.from(bottomSheet)
        //getFragmentManager()
        val fragmentManager: FragmentManager = fragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_parent, MainFragment.newInstance(MESSAGECODE, "p2"))
        fragmentTransaction.commit()
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        var result = true
        when (id) {
            android.R.id.home -> finish()
            else -> result = super.onOptionsItemSelected(item)
        }
        return result
    }

    override fun OnShowChild(filepath: String, _objectID: String, boolean: Boolean) {
        val ft = supportFragmentManager.beginTransaction()
        this.fileEntity = fileEntity
        ft.replace(R.id.child_fragment, ChildMainFragment.newInstance(filepath, _objectID, boolean))
        ft.commit()
        behavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCloseChildMain() {
        behavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }
    override fun onFragmentInteraction(uri: Uri) {

    }
}
