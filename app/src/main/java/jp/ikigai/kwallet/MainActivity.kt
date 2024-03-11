package jp.ikigai.kwallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import jp.ikigai.kwallet.ui.BaseScreen
import jp.ikigai.kwallet.ui.theme.KWalletTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KWalletTheme {
                KoinAndroidContext {
                    BaseScreen()
                }
            }
        }
    }
}
