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

    val mediaPlayer = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)

        // Library initilisation


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
        context?.let { Vault(it).initialize() }
        // TODO: Use the ViewModel
    }

    fun decryptFile(): String {
        val url = "https://arweave.net/zPUcS81IIrJQ0arZjBBoAyOW_bG1PSKxKXPNEL2eKa0"
        val symmetricKey = "54d61c43b9832ce5652b09404ceddfae93603004aa4684a86bf64bcbdf9e6a16"

        val data = fetchEncryptedData(url).get()



        val tempMp3: File = File.createTempFile("kurchina", "mp3")
        tempMp3.deleteOnExit()
        val fos = FileOutputStream(tempMp3)
        fos.write(decryptedFile)
        fos.close()

        val fis = FileInputStream(tempMp3)
        mediaPlayer.setDataSource(fis.fd)
        mediaPlayer.prepare()
        return "No. Tracks: ${mediaPlayer.trackInfo.size}.\n"
    }

    fun playPauseMedia() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

}