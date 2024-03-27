package cz.rimu.prodlouzenevikendy.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import cz.rimu.prodlouzenevikendy.data.LocalHolidayCountRepository
import cz.rimu.prodlouzenevikendy.data.RemotePublicHolidayRepository
import cz.rimu.prodlouzenevikendy.domain.HolidayCountRepository
import cz.rimu.prodlouzenevikendy.domain.PublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.Api
import cz.rimu.prodlouzenevikendy.presentation.HolidayListViewModel
import cz.rimu.prodlouzenevikendy.presentation.HolidayCountViewModel
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

    factoryOf(::RemotePublicHolidayRepository) bind PublicHolidaysRepository::class
    factoryOf(::LocalHolidayCountRepository) bind HolidayCountRepository::class
}