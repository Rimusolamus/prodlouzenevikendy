package cz.rimu.prodlouzenevikendy.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import cz.rimu.prodlouzenevikendy.data.MemoryLocalHolidayCountRepository
import cz.rimu.prodlouzenevikendy.data.MemorySelectedRecommendations
import cz.rimu.prodlouzenevikendy.data.RetrofitPublicHolidayRepository
import cz.rimu.prodlouzenevikendy.domain.LocalHolidayCountRepository
import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendations
import cz.rimu.prodlouzenevikendy.domain.RemotePublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.Api
import cz.rimu.prodlouzenevikendy.presentation.HolidayListViewModel
import cz.rimu.prodlouzenevikendy.presentation.HolidayCountViewModel
import cz.rimu.prodlouzenevikendy.presentation.SelectedHolidaysViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    factory { provideRestApi(get()) }
    single { provideRetrofit() }
}

private fun provideRetrofit(): Retrofit {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder().baseUrl("https://date.nager.at/api/v3/")
        .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
}

private fun provideRestApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

val appModule = module {
    viewModelOf(::HolidayCountViewModel)
    viewModelOf(::HolidayListViewModel)
    viewModelOf(::SelectedHolidaysViewModel)

    factoryOf(::RetrofitPublicHolidayRepository) bind RemotePublicHolidaysRepository::class
    singleOf(::MemoryLocalHolidayCountRepository) bind LocalHolidayCountRepository::class
    singleOf(::MemorySelectedRecommendations) bind LocalSelectedRecommendations::class
}