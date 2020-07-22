package com.videodac.hls.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.videodac.hls.R
import org.kethereum.crypto.createEthereumKeyPair
import org.kethereum.keystore.api.InitializingFileKeyStore
import org.kethereum.keystore.api.KeyStore
import org.kethereum.model.ECKeyPair
import org.kethereum.model.PrivateKey
import org.kethereum.model.PublicKey
import org.komputing.khex.model.HexString

import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.io.File

object Utils {

    internal const val WALLET_CREATED = "WALLET_CREATED"
    internal const val WALLET_PATH = "WALLET_PATH"
    internal const val streamingFeeInEth = 0.0002
    internal const val recipientAddress = "0xdac817294c0c87ca4fa1895ef4b972eade99f2fd"
    internal const val walletPassword = "password"
    internal lateinit var walletPublicKey: String
    internal const val STREAM_URL = "http://52.29.226.43:8935/stream/hello_world.m3u8"

    // burner wallet configs
    const val ACCOUNT_TYPE_NONE = "none"
    const val DEFAULT_PASSWORD = "default"

    @JvmStatic
    internal fun goFullScreen(activity: AppCompatActivity) {
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT in 12..18) { // lower api
            val v = activity.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) { //for new api versions.
            val decorView = activity.window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

    @JvmStatic
    internal fun getWeb3(activity: AppCompatActivity) = Web3j.build(HttpService(activity.getString(R.string.infura_url)))

    @JvmStatic
    internal fun closeActivity(activity: AppCompatActivity, userAddress: String?) {
        if (!userAddress.isNullOrEmpty()) {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User Address", userAddress)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(activity, "Address copied to clipboard" , Toast.LENGTH_LONG).show()
        }
        // finish activity
        activity.finish()
    }

    @JvmStatic
    internal fun createBurnerWallet(context: Context) {
        val currentSpec = AccountKeySpec(ACCOUNT_TYPE_NONE)
        val keyStore by lazy { InitializingFileKeyStore(File(context.filesDir, "keystore")) }

        val importKey = currentSpec.initPayload?.let {
            val split = it.split("/")
            val privateKey = PrivateKey(HexString(split.first()))
            val publicKey = PublicKey(HexString(split.last()))
            ECKeyPair(privateKey, publicKey)
        }

        val key = importKey ?: createEthereumKeyPair()

        keyStore.addKey(key, DEFAULT_PASSWORD, true)
    }

}
