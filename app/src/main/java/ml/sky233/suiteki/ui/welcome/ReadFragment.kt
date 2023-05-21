package ml.sky233.suiteki.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ml.sky233.suiteki.R
import ml.sky233.suiteki.databinding.FragmentWelcomeMainBinding
import ml.sky233.suiteki.databinding.FragmentWelcomeReadBinding


class ReadFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeReadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWelcomeReadBinding.inflate(inflater, container, false)
        return binding.root
    }

}