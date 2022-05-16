package com.bintangpoetra.thisable.presentation.profile.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.R.string
import com.bintangpoetra.thisable.databinding.FragmentDetailProfileBinding

class DetailProfileFragment: Fragment() {

    private var _fragmentDetailProfileBinding: FragmentDetailProfileBinding? = null
    private val binding get() = _fragmentDetailProfileBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentDetailProfileBinding = FragmentDetailProfileBinding.inflate(inflater, container, false)
        return _fragmentDetailProfileBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        initToolbar()
    }

    private fun initToolbar() {
        binding.toolbar.apply {
            title = context.getString(string.title_my_account)
            navigationIcon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

}