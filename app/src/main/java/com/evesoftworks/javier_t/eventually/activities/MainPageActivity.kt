package com.evesoftworks.javier_t.eventually.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.TextView
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.SectionsPagerAdapter
import com.evesoftworks.javier_t.eventually.fragments.GroupsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.tabs_layout.*

class MainPageActivity : AppCompatActivity(), GroupsFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {
    lateinit var header: View
    lateinit var profilePic: CircleImageView
    lateinit var profileDisplayName: TextView
    lateinit var profileEmail: TextView
    lateinit var userData: ArrayList<String>
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var mToggle: ActionBarDrawerToggle

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        when (id) {
            R.id.my_events -> {

            }

            R.id.my_profile -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("USERDATA", userData)
                startActivity(intent)
            }

            R.id.action_settings -> {

            }

            R.id.share_friends -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "Descárgate Eventually, está genial! ;)")
                intent.type = "text/plain"
                startActivity(intent)
            }

            R.id.feedback -> {

            }

            R.id.log_out -> {
                confirmDialog()
            }
        }

        main_content.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val bundle = intent.extras
        continueActivityFlowIfTheresDynamicLink(bundle)

        container.offscreenPageLimit = 3;
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        header = (findViewById<NavigationView>(R.id.navigation_drawer)).getHeaderView(0)

        profilePic = header.findViewById(R.id.profile_pic_drawer)
        profileDisplayName = header.findViewById(R.id.nav_username)
        profileEmail = header.findViewById(R.id.nav_email)

        mToggle = ActionBarDrawerToggle(this, main_content, R.string.sidebaropen, R.string.sidebarclosed)
        main_content.addDrawerListener(mToggle)
        mToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigation_drawer.setNavigationItemSelectedListener(this)

        setDataWithCurrentUser()

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mToggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            R.id.action_search -> {
                circleReveal(R.id.toolbar, 1, true, true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_searching, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val mSearchView: SearchView = searchItem?.actionView as SearchView
        mSearchView.queryHint = getString(R.string.search_event_hint)

        return true
    }

    private fun setDataWithCurrentUser() {
        userData = ArrayList()

        FirebaseAuth.getInstance().currentUser?.let {
            Picasso.get().load(it.photoUrl).into(profilePic)
            profileDisplayName.text = it.displayName.toString()
            profileEmail.text = it.email.toString()

            userData.add(it.displayName.toString())
            userData.add(it.email.toString())

            val db: FirebaseFirestore = FirebaseFirestore.getInstance()

            db.collection("Usuarios").document(it.uid).get().addOnSuccessListener {
                userData.add(it.getString("username")!!)
            }
        }


    }

    private fun goToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

        val count = fragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }

    }

    private fun confirmDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_log_out))
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton(getString(R.string.logout_ok), DialogInterface.OnClickListener { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    goToSignInActivity()
                    finish()
                })
                .setNegativeButton(getString(R.string.logout_cancel), null).show()
    }

    private fun circleReveal(viewId: Int, startingPos: Int, hasOverflow: Boolean, isShow: Boolean) {
        val view = findViewById<View>(viewId)
        var width = view.width

        if (startingPos > 0) {
            width -= (startingPos * resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))
        }

        if (hasOverflow) {
            width -= resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
        }

        val centerX = width
        val centerY = view.height / 2
        val anim: Animator

        if (isShow) {
            anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0f, width.toFloat())
        } else {
            anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, width.toFloat(), 0f)
        }

        anim.duration = 220L

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.INVISIBLE
                }
            }
        })

        if (isShow) {
            view.visibility = View.VISIBLE
        }

        anim.start()
    }

    private fun continueActivityFlowIfTheresDynamicLink(bundle: Bundle?) {
        if (bundle?.get("DYN_LINK") != null) {
            val intent = Intent(this, AnEventActivity::class.java)
            intent.putExtra("DYN_LINK", bundle.getString("DYN_LINK"))
            startActivity(intent)
        }
    }
}
