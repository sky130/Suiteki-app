package ml.sky233.suiteki.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ml.sky233.suiteki.R
import ml.sky233.suiteki.bean.PermissionObject
import ml.sky233.suiteki.databinding.ActivityWelcomeBinding
import ml.sky233.suiteki.ui.welcome.DeviceFragment
import ml.sky233.suiteki.ui.welcome.MainFragment
import ml.sky233.suiteki.ui.welcome.PermissionFragment
import ml.sky233.suiteki.ui.welcome.ReadFragment
import ml.sky233.suiteki.ui.welcome.TipsFragment
import ml.sky233.suiteki.util.SettingUtils
import ml.sky233.suiteki.util.ActivityUtils.startActivity
import ml.sky233.suiteki.util.ActivityUtils.barTextToBlack

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    val fragmentList: List<Fragment> = ArrayList<Fragment>().apply {
        add(MainFragment())
        add(ReadFragment())
        add(PermissionFragment())
        add(
            DeviceFragment(
                ArrayList<PermissionObject>().apply {
                    add(PermissionObject(R.drawable.ic_band, "小米运动健康", ""))
                    add(PermissionObject(R.drawable.ic_band, "ZeppLife", ""))
                    add(PermissionObject(R.drawable.ic_band, "手动输入添加", ""))
                },
                supportFragmentManager
            )
        )
        add(TipsFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barTextToBlack()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPage.adapter = AppFragmentPageAdapter(supportFragmentManager, fragmentList)
        binding.next.setOnClickListener {
            it as Button
            if (binding.viewPage.currentItem + 1 == fragmentList.size) {
                SettingUtils.setBoolean("first_start", true)
                startActivity<MainActivity>()
                finish()
            }
            if (binding.viewPage.currentItem + 2 == fragmentList.size) {
                it.text = "完成"
            }
            binding.back.visibility = View.VISIBLE
            binding.viewPage.setCurrentItem(binding.viewPage.currentItem + 1, true)
        }
        binding.back.setOnClickListener {
            if (binding.viewPage.currentItem - 1 == 0) {
                it.visibility = View.INVISIBLE
            }
            binding.next.text = "继续"
            binding.viewPage.setCurrentItem(binding.viewPage.currentItem - 1, true)
        }
    }

    @Suppress("DEPRECATION")
    class AppFragmentPageAdapter(fm: FragmentManager?, private var mFragmentList: List<Fragment>?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return mFragmentList!![position]
        }

        override fun getCount(): Int {
            return if (mFragmentList == null) 0 else mFragmentList!!.size
        }
    }
}