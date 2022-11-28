package fan.vault.vault.ui.main

import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fan.vault.sdk.Vault
import fan.vault.vault.databinding.MainFragmentBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    private lateinit var vault: Vault

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)

        val view = binding.root
        binding.getPublicKeyButton.setOnClickListener {
            binding.getPublicKeyText.text = vault.getAppWalletPublicKey()
        }
        binding.getOtpButton.setOnClickListener {
            binding.otpInput.setText(vault.getOtp() ?: "Not found")
        }
        binding.saveOtpButton.setOnClickListener {
            vault.saveOtp(binding.otpInput.text.toString())
            binding.otpInput.setText("")
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        context?.let {
            vault = Vault(it)
            vault.initialize()
        }
        // TODO: Use the ViewModel
    }

}