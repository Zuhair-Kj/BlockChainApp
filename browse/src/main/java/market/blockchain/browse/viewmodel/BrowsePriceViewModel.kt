package market.blockchain.browse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import market.blockchain.browse.api.GetPriceInfoApi
import market.blockchain.browse.model.PriceInfo
import market.blockchain.browse.model.PriceInfoRequestParams
import market.blockchain.core.util.NetworkHelper
import market.blockchain.core.util.Resource

class BrowsePriceViewModel(
    private val priceInfoApi: GetPriceInfoApi,
    private val networkHelper: NetworkHelper,
    private val mutableStateLiveData: MutableLiveData<Resource<PriceInfo>> = MutableLiveData(),
    private val testScope: CoroutineScope? = null,
    private val testDispatcher: CoroutineDispatcher? = null
): ViewModel() {
    val stateLiveData: LiveData<Resource<PriceInfo>>
    get() = mutableStateLiveData

    val requestParams = PriceInfoRequestParams(4, 1)

    fun getPricesInfo() {
        val currentValue = stateLiveData.value?.data
        (testScope ?: viewModelScope).launch(testDispatcher ?: Dispatchers.IO) {
            try {
                mutableStateLiveData.postValue(Resource.loading(currentValue))
                if (networkHelper.isConnected()) {
                    val result = priceInfoApi.getBitCoinPrice(requestParams.convertToMap())
                    mutableStateLiveData.postValue(Resource.success(result))
                } else {
                    mutableStateLiveData.postValue(Resource.networkError(currentValue))
                }
            } catch (throwable: Throwable) {
                mutableStateLiveData.postValue(Resource.error(currentValue, throwable.message))
            }
        }
    }
}