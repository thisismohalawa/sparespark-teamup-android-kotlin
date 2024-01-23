package sparespark.teamup.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sparespark.teamup.common.binding.ViewBindingHolder
import sparespark.teamup.common.binding.ViewBindingHolderImpl
import sparespark.teamup.databinding.LoginViewBinding

class LoginView() : Fragment(),
    View.OnClickListener,
    ViewBindingHolder<LoginViewBinding> by ViewBindingHolderImpl() {

    override fun onClick(view: View?) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(LoginViewBinding.inflate(layoutInflater), this@LoginView) {

        setUpViewClickListener()
    }

    private fun setUpViewClickListener() {

    }
}
