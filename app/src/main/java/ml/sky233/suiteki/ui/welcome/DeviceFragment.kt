package ml.sky233.suiteki.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import ml.sky233.choseki.adapter.AppAdapter
import ml.sky233.suiteki.adapter.RecyclerExtras
import ml.sky233.suiteki.bean.PermissionObject
import ml.sky233.suiteki.databinding.FragmentWelcomeDeviceBinding
import ml.sky233.suiteki.ui.dialog.CustomAddDeviceBottomSheet
import ml.sky233.suiteki.ui.dialog.ZeppLoginBottomSheet


class DeviceFragment(list: ArrayList<PermissionObject>,fm: FragmentManager) : Fragment() {
    private lateinit var binding: FragmentWelcomeDeviceBinding
    private val appList: ArrayList<PermissionObject> = list
    private val supportFragmentManager = fm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWelcomeDeviceBinding.inflate(inflater, container, false)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = AppAdapter(requireContext(), appList).apply {
            setOnItemClickListener(object : RecyclerExtras.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    when (position){
                        1 ->{
                            ZeppLoginBottomSheet(supportFragmentManager).show(supportFragmentManager,ZeppLoginBottomSheet.TAG)
                        }
                        2 ->{
                            CustomAddDeviceBottomSheet().show(supportFragmentManager,CustomAddDeviceBottomSheet.TAG)
                        }
                    }
                }
            })
        }

        return binding.root
    }

}