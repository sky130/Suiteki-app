package ml.sky233.suiteki.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ml.sky233.suiteki.R
import ml.sky233.suiteki.databinding.ActivityMainBinding
import ml.sky233.suiteki.sqlite.SuitekiDataGetter
import ml.sky233.suiteki.ui.dialog.DeviceBottomSheet
import ml.sky233.suiteki.util.PermissionUtils
import ml.sky233.suiteki.util.SettingUtils
import ml.sky233.suiteki.util.TextUtils.toast
import ml.sky233.suiteki.util.ActivityUtils.startActivity
import ml.sky233.suiteki.util.ActivityUtils.barTextToBlack
import ml.sky233.suiteki.util.ActivityUtils.startFor

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var device = SettingUtils.getInt(SettingUtils.DEVICE)

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barTextToBlack()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Suiteki"
        setSupportActionBar(binding.toolbar)
        binding.yBtn.setOnClickListener {
            PermissionUtils.apply {
                requestBlePermissions(this@MainActivity, 2)
                requestWritePermissions(this@MainActivity, 1)
                startFor(MI_HEALTH_PACKAGE_NAME, 0)
            }
        }
        binding.deviceChange.setOnClickListener { _ ->
            val sheet = DeviceBottomSheet { getDevice() }
            sheet.show(supportFragmentManager, DeviceBottomSheet.TAG)
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (requestCode) {
            0 -> {
                if (resultCode == RESULT_OK) {
                    PermissionUtils.saveUriPermission(data)
                }
            }

            1 -> {
                if (resultCode != RESULT_OK) {
                    "授权失败".toast()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_settings -> startActivity<SettingsActivity>()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.isGetAllPermission()) {
            binding.cardView.visibility = View.GONE
        }else{
            binding.cardView.visibility = View.VISIBLE
            binding.titleCard.text = "权限未获取"
            binding.subTextCard.text = PermissionUtils.getPermissionSubTitle()
        }
        getDevice()
    }

    private fun getDevice() {
        device = SettingUtils.getInt(SettingUtils.DEVICE)
        if (device != -1 && device <= SuitekiDataGetter.device.size) {
            binding.cardTitleName.text = SuitekiDataGetter.device[device].name
            binding.cardTitleMac.text = SuitekiDataGetter.device[device].mac
        } else {
            binding.cardTitleName.text = "未选择设备"
            binding.cardTitleMac.text = "请先选择设备"
        }
    }

}