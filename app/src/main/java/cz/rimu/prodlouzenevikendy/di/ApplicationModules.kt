package cz.rimu.prodlouzenevikendy.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import cz.rimu.prodlouzenevikendy.data.MemorySelectedRecommendationsRepository
import cz.rimu.prodlouzenevikendy.data.RetrofitPublicHolidayRepository
import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendationsRepository
import cz.rimu.prodlouzenevikendy.domain.RemotePublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.Api
import cz.rimu.prodlouzenevikendy.presentation.HolidayListViewModel
import cz.rimu.prodlouzenevikendy.presentation.SelectedHolidaysViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
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
    viewModelOf(::HolidayListViewModel)
    viewModelOf(::SelectedHolidaysViewModel)

    factoryOf(::RetrofitPublicHolidayRepository) bind RemotePublicHolidaysRepository::class
    singleOf(::MemorySelectedRecommendationsRepository) bind LocalSelectedRecommendationsRepository::class
}