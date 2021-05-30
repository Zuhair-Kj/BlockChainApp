package market.blockchain.browse.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import market.blockchain.browse.api.GetPriceInfoApi
import market.blockchain.browse.model.PriceInfo
import market.blockchain.browse.model.PriceInfoRequestParams
import market.blockchain.core.Resource

class BrowsePriceViewModel(
    private val priceInfoApi: GetPriceInfoApi,
    private val mutableStateLiveData: MutableLiveData<Resource<PriceInfo>> = MutableLiveData()
): ViewModel() {
    val stateLiveData: LiveData<Resource<PriceInfo>>
    get() = mutableStateLiveData

    val requestParams = PriceInfoRequestParams(4)

    fun getPricesInfo() {
        val currentValue = stateLiveData.value?.data ?: PriceInfo()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mutableStateLiveData.postValue(Resource.loading(currentValue))
                val result = priceInfoApi.getBitCoinPrice(requestParams.convertToMap())
                mutableStateLiveData.postValue(Resource.success(result))
            } catch (throwable: Throwable) {
                mutableStateLiveData.postValue(Resource.error(currentValue, throwable.message ?: ""))
            }
        }
    }
}