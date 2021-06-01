package market.blockchain.browse.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import market.blockchain.browse.api.GetPriceInfoApi
import market.blockchain.browse.model.PriceInfo
import market.blockchain.core.util.NetworkHelper
import market.blockchain.core.util.Resource
import market.blockchain.test.UnitTestState
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BrowsePriceViewModelTest {
    @Mock
    lateinit var priceInfoApi: GetPriceInfoApi
    @Mock
    lateinit var networkHelper: NetworkHelper

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    private val testScope = TestCoroutineScope()

    private lateinit var viewModel: BrowsePriceViewModel
    private val mutableLiveData = MutableLiveData<Resource<PriceInfo>>()

    @Spy
    private val unitTestState = UnitTestState<Resource<PriceInfo>>()

    @Before
    fun init() {
        viewModel = BrowsePriceViewModel(
            priceInfoApi, networkHelper,
            mutableLiveData, testScope, testDispatcher
        )
    }

    @Test
    fun `must fetch results from api`() {
        viewModel.stateLiveData.observeForever {
            unitTestState.obj = it
        }

        val mockResponse = mock(PriceInfo::class.java)
        `when`(networkHelper.isConnected()).thenReturn(true)
        testDispatcher.runBlockingTest {
            `when`(priceInfoApi.getBitCoinPrice(ArgumentMatchers.anyMap())).thenReturn(mockResponse)
            viewModel.getPricesInfo()
            verify(priceInfoApi).getBitCoinPrice(viewModel.requestParams.convertToMap())
            verify(unitTestState).obj = Resource.loading()
            verify(unitTestState).obj = Resource.success(mockResponse)
        }
    }

    @Test
    fun `must load and return error with existing data`() {
        viewModel.stateLiveData.observeForever {
            unitTestState.obj = it
        }

        val mockResponse = mock(PriceInfo::class.java)
        mutableLiveData.postValue(Resource.success(mockResponse))
        `when`(networkHelper.isConnected()).thenReturn(true)
        testDispatcher.runBlockingTest {
            `when`(priceInfoApi.getBitCoinPrice(ArgumentMatchers.anyMap())).thenThrow(
                RuntimeException("Test")
            )
            viewModel.getPricesInfo()
            verify(priceInfoApi).getBitCoinPrice(viewModel.requestParams.convertToMap())
            verify(unitTestState).obj = Resource.loading(mockResponse)
            verify(unitTestState).obj = Resource.error(mockResponse, "Test")
        }
    }

    @Test
    fun `must load and return network error with existing data`() {
        viewModel.stateLiveData.observeForever {
            unitTestState.obj = it
        }

        val mockResponse = mock(PriceInfo::class.java)
        mutableLiveData.postValue(Resource.error(mockResponse))
        `when`(networkHelper.isConnected()).thenReturn(false)
        testDispatcher.runBlockingTest {
            viewModel.getPricesInfo()
            verifyNoInteractions(priceInfoApi)
            verify(unitTestState).obj = Resource.loading(mockResponse)
            verify(unitTestState).obj = Resource.networkError(mockResponse)
        }
    }
}