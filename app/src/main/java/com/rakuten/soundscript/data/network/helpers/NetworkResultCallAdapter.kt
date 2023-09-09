package com.udbhav.blackend.ramayana.data.network.helpers

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import java.net.Proxy

class NetworkResultCallAdapter(
    private val resultType: Type
) : CallAdapter<Proxy.Type, Call<NetworkResult<Proxy.Type>>> {

    override fun responseType(): Type = resultType

    override fun adapt(call: Call<Proxy.Type>): Call<NetworkResult<Proxy.Type>> {
        return NetworkResultCall(call)
    }
}