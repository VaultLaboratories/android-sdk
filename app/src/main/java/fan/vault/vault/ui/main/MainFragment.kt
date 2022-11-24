package fan.vault.vault.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fan.vault.sdk.Vault
import fan.vault.vault.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        binding.getPublicKeyButton.setOnClickListener {
            binding.getPublicKeyText.text = Vault.getAppWalletPublicKey()
        }
        binding.decryptFile.setOnClickListener {
            binding.trackInfo.text = Vault.decryptFile()
        }
        binding.playPause.setOnClickListener {
            Vault.playPauseMedia()
        }
        binding.getOtpButton.setOnClickListener {
            binding.otpInput.setText(Vault.getOtp() ?: "Not found")
        }
        binding.saveOtpButton.setOnClickListener {
            Vault.saveOtp(binding.otpInput.text.toString())
            binding.otpInput.setText("")
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        context?.let { Vault.initialize(it) }
        // TODO: Use the ViewModel
    }

}