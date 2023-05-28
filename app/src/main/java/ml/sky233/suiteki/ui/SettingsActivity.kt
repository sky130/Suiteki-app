package ml.sky233.suiteki.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import ml.sky233.suiteki.R
import ml.sky233.suiteki.databinding.ActivitySettingsBinding
import ml.sky233.suiteki.util.ActivityUtils.barTextToBlack

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barTextToBlack()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (this.applicationContext.resources.configuration.uiMode != 0x21) {
            binding.toolbar.overflowIcon?.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.black
                ), PorterDuff.Mode.SRC_ATOP
            )
            binding.toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        binding.collapsingToolbar.title
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View {
            val view = super.onCreateView(inflater, container, savedInstanceState)
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.behavior = AppBarLayout.ScrollingViewBehavior()
            view.layoutParams = params
            return view
        }

        override fun onCreateRecyclerView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            savedInstanceState: Bundle?
        ): RecyclerView {
            val view = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
            view.scrollBarSize=0
            return view
        }

        override fun setDivider(divider: Drawable?) {
            super.setDivider(ColorDrawable(Color.TRANSPARENT))
        }

        override fun setDividerHeight(height: Int) {
            super.setDividerHeight(0)
        }

    }
}