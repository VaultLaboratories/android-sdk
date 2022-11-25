package fan.vault.vault.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fan.vault.sdk.Vault
import fan.vault.vault.databinding.MainFragmentBinding
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        binding.getNftsInSocialWalletButton.setOnClickListener {
            GlobalScope.launch {
                val socialWallet = Vault.getSocialWallet2(binding.emailAddressInput.text.toString())
                val nftsWithMetadata = Vault.getNftsForWalletAddress(socialWallet.wallet)
                var output = ""
                nftsWithMetadata.forEach {
                    it.metadata?.name?.let { nftName ->
                        output += nftName
                        Log.d("Vault", nftName)
                    }
                }
                binding.socialWalletNftsDisplay.text = output
            }
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