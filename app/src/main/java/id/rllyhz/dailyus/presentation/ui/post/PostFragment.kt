package id.rllyhz.dailyus.presentation.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.rllyhz.dailyus.databinding.FragmentPostBinding

class PostFragment : Fragment() {
    private var binding: FragmentPostBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}