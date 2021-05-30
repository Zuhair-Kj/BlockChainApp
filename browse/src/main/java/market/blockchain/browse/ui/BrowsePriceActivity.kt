package market.blockchain.browse.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import market.blockchain.browse.R
import market.blockchain.browse.model.PriceInfo
import market.blockchain.browse.viewmodel.BrowsePriceViewModel
import market.blockchain.core.Resource
import market.blockchain.core.Resource.Status.ERROR
import market.blockchain.core.Resource.Status.LOADING
import market.blockchain.core.Resource.Status.NETWORK_DISCONNECTED
import market.blockchain.core.Resource.Status.SUCCESS
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrowsePriceActivity : AppCompatActivity() {
    private val browsePriceViewModel: BrowsePriceViewModel by viewModel()
    private val browsePriceObserver = Observer<Resource<PriceInfo>> { resource ->
        when(resource.status) {
            SUCCESS -> {

            }
            LOADING -> {

            }
            ERROR -> {

            }
            NETWORK_DISCONNECTED -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_price)
        browsePriceViewModel.stateLiveData.observe(this, browsePriceObserver)
        browsePriceViewModel.getPricesInfo()
    }
}