package market.blockchain.browse.injection

import market.blockchain.browse.api.GetPriceInfoApi
import market.blockchain.browse.viewmodel.BrowsePriceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SCOPE_DISCOVERY = "discovery"
const val BASE_URL = "https://api.blockchain.info"

@JvmField
val browseModule = module {
    single<GetPriceInfoApi> {
        get<Retrofit>(named(SCOPE_DISCOVERY)).create(GetPriceInfoApi::class.java)
    }

    single(named(SCOPE_DISCOVERY)) { Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(get())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    }

    viewModel {
        BrowsePriceViewModel(priceInfoApi = get())
    }
}
