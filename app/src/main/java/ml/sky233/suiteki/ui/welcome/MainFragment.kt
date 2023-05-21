package ml.sky233.suiteki.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ml.sky233.suiteki.R
import ml.sky233.suiteki.databinding.FragmentWelcomeMainBinding


class MainFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWelcomeMainBinding.inflate(inflater, container, false)
        return binding.root
    }

}