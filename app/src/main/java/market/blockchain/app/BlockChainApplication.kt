package market.blockchain.app

import android.app.Application
import market.blockchain.browse.injection.browseModule
import market.blockchain.core.injection.networkModule
import org.koin.android.ext.koin.androidContext

class BlockChainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        org.koin.core.context.startKoin {
            androidContext(this@BlockChainApplication)
            modules(
                listOf(networkModule, browseModule)
            )

        }
    }
}