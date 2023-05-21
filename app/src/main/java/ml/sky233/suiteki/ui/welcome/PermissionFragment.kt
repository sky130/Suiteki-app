package ml.sky233.suiteki.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ml.sky233.choseki.adapter.PermissionAdapter
import ml.sky233.suiteki.R
import ml.sky233.suiteki.databinding.FragmentWelcomeMainBinding
import ml.sky233.suiteki.databinding.FragmentWelcomePermissionBinding
import ml.sky233.suiteki.databinding.FragmentWelcomeReadBinding
import ml.sky233.suiteki.util.PermissionUtils


class PermissionFragment : Fragment() {
    private lateinit var binding: FragmentWelcomePermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWelcomePermissionBinding.inflate(inflater, container, false)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter =
            PermissionAdapter(requireContext(), PermissionUtils.PERMISSION_LIST)
        return binding.root
    }

}