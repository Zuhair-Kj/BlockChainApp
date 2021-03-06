package market.blockchain.core.injection

import market.blockchain.core.util.NetworkHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

@JvmField
val coreModule = module {
    single {
        NetworkHelper(androidContext())
    }
}
@JvmField
val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY }
    }
}