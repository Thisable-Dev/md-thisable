package com.bintangpoetra.thisable.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.R.string
import com.bintangpoetra.thisable.data.remote.ApiResponse
import com.bintangpoetra.thisable.data.remote.auth.login.LoginBody
import com.bintangpoetra.thisable.databinding.FragmentLoginBinding
import com.bintangpoetra.thisable.utils.ConstVal.KEY_EMAIL
import com.bintangpoetra.thisable.utils.ConstVal.KEY_IS_LOGIN
import com.bintangpoetra.thisable.utils.ConstVal.KEY_TOKEN
import com.bintangpoetra.thisable.utils.ConstVal.KEY_USER_ID
import com.bintangpoetra.thisable.utils.ConstVal.KEY_USER_NAME
import com.bintangpoetra.thisable.utils.SharedPrefManager
import com.bintangpoetra.thisable.utils.ext.isEmailValid
import com.bintangpoetra.thisable.utils.ext.showOKDialog
import com.bintangpoetra.thisable.utils.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    private var _fragmentLoginBinding: FragmentLoginBinding? = null
    private val binding get() = _fragmentLoginBinding!!

    private lateinit var pref: SharedPrefManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return _fragmentLoginBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())

        initAction()
    }

    private fun initAction() {
        binding.tvRegisterHere.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener {
            val userEmail = binding.edtEmail.text.toString()
            val userPassword = binding.edtPassword.text.toString()

            when {
                userEmail.isBlank() -> {
                    binding.edtEmail.requestFocus()
                    binding.edtEmail.error = "Email tidak boleh kosong"
                }
                !userEmail.isEmailValid() -> {
                    binding.edtEmail.requestFocus()
                    binding.edtEmail.error = "Email tidak valid"
                }
                userPassword.isBlank() -> {
                    binding.edtPassword.requestFocus()
                    binding.edtPassword.error = "Password tidak boleh kosong"
                }
                else -> {
                    val loginBody = LoginBody(
                        email = userEmail,
                        password = userPassword
                    )
                    loginUser(loginBody)
                }
            }
        }
    }

    private fun initUI() {
    }

    private fun loginUser(loginBody: LoginBody) {
        loginViewModel.loginUser(loginBody).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                }
                is ApiResponse.Success -> {
                    try {
                        showLoading(false)
                        val userData = response.data.loginResult
                        pref.apply {
                            setStringPreference(KEY_USER_ID, userData.userId)
                            setStringPreference(KEY_TOKEN, userData.token)
                            setStringPreference(KEY_USER_NAME, userData.name)
                            setStringPreference(KEY_EMAIL, loginBody.email)
                            setBooleanPreference(KEY_IS_LOGIN, true)
                        }
                    } finally {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
                is ApiResponse.Error -> {
                    showLoading(false)
                    context?.showOKDialog(
                        getString(string.message_information),
                        getString(string.message_login_failed)
                    )
                }
                else -> {
                    context?.showToast(getString(string.message_unknown_state))
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.bgDim.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtEmail.isClickable = !isLoading
        binding.edtEmail.isEnabled = !isLoading
        binding.edtPassword.isClickable = !isLoading
        binding.edtPassword.isEnabled = !isLoading
        binding.btnLogin.isClickable = !isLoading
    }
}