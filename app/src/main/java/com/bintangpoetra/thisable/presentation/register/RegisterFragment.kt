package com.bintangpoetra.thisable.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.data.remote.ApiResponse
import com.bintangpoetra.thisable.data.remote.auth.register.RegisterBody
import com.bintangpoetra.thisable.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private var _fragmentRegisterBinding: FragmentRegisterBinding? = null
    private val binding get() = _fragmentRegisterBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return _fragmentRegisterBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
    }

    private fun initAction() {
        binding.tvLoginHere.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun registerUser(userData: RegisterBody) {
        registerViewModel.registerUser(userData).observe(viewLifecycleOwner) { result ->
            when(result) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                }

            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.bgDim.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtFullName.isClickable = !isLoading
        binding.edtFullName.isEnabled = !isLoading
        binding.edtEmail.isClickable = !isLoading
        binding.edtEmail.isEnabled = !isLoading
        binding.edtPassword.isClickable = !isLoading
        binding.edtPassword.isEnabled = !isLoading
        binding.btnRegister.isClickable = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentRegisterBinding = null
    }

}