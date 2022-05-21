package com.bintangpoetra.thisable.presentation.bugreport

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.databinding.FragmentBugReportBinding

class BugReportFragment: Fragment() {

    private var _fragmentReportBinding: FragmentBugReportBinding? = null
    private val binding get() = _fragmentReportBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentReportBinding = FragmentBugReportBinding.inflate(inflater, container, false)
        return _fragmentReportBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initAction()
    }

    private fun initUI() {
        binding.toolbar.apply {
            navigationIcon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

    private fun initAction() {
        binding.edtReport.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvCharCounter.text = char?.length.toString() + "/300"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

}