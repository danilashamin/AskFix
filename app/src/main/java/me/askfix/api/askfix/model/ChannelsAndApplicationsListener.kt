package me.askfix.api.askfix.model

import retrofit2.Call

interface ChannelsAndApplicationsListener {
    fun onChannelsAndApplicationsResponse(channelsResponse: ChannelsResponse?)
    fun onError(call: Call<ChannelsResponse>, t: Throwable)
}