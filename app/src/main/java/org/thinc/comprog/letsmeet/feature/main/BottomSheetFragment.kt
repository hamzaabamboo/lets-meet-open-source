package org.thinc.comprog.letsmeet.feature.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.databinding.FragmentBottomSheetDialogBinding

class BottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var viewModel: MainActivityViewModel
    lateinit var binding: FragmentBottomSheetDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.user.value = arguments!!.getParcelable("user")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewModel.activity = context as MainActivity
    }
}