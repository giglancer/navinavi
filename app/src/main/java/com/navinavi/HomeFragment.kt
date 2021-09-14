package com.navinavi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.navinavi.databinding.FragmentHomeBinding
import com.savvyapps.togglebuttonlayout.Toggle
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout

class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleButtonView()
        toggleButtonClickLister()


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    トグルボタン設定
    private fun toggleButtonView() {
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(1, null, "出発"))
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(2, null, "到着"))
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(3, null, "始発"))
        binding.arrivalToLastTrainToggleButton.addToggle(Toggle(4, null, "終電"))

//     デフォルトは出発
        binding.arrivalToLastTrainToggleButton.setToggled(1, true)
    }

    private fun toggleButtonClickLister() {
        binding.arrivalToLastTrainToggleButton.onToggledListener = {_, toggle, selected ->
            if (toggle.isSelected) {
                when (toggle.id) {
                    1 -> Log.d(TAG, toggle.title.toString())
                    2 -> Log.d(TAG, toggle.title.toString())
                    3 -> Log.d(TAG, toggle.title.toString())
                    4 -> Log.d(TAG, toggle.title.toString())
                }
            }
        }
    }
}