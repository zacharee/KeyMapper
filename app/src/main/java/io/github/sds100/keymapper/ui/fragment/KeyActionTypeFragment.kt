package io.github.sds100.keymapper.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import io.github.sds100.keymapper.data.viewmodel.KeyActionTypeViewModel
import io.github.sds100.keymapper.databinding.FragmentKeyActionTypeBinding
import io.github.sds100.keymapper.util.InjectorUtils

/**
 * Created by sds100 on 30/03/2020.
 */

class KeyActionTypeFragment : Fragment() {
    companion object {
        const val REQUEST_KEY = "request_key"
        const val EXTRA_KEYCODE = "extra_keycode"
    }

    private val mViewModel: KeyActionTypeViewModel by activityViewModels {
        InjectorUtils.provideKeyActionTypeViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        FragmentKeyActionTypeBinding.inflate(inflater, container, false).apply {

            lifecycleOwner = viewLifecycleOwner
            mViewModel.clearKey()

            viewModel = mViewModel

            setOnDoneClick {
                setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_KEYCODE to mViewModel.keyEvent.value?.keyCode))
                findNavController().navigateUp()
            }

            return this.root
        }
    }
}