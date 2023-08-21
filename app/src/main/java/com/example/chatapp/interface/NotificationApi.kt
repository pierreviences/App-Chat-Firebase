package com.example.chatapp.`interface`

import com.example.chatapp.constans.Constans.Companion.SERVER_KEY
import com.example.chatapp.constans.Constans.Companion.CONTENT_TYPE
import com.example.chatapp.model.PushNotification
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
        @Headers("Authorization: key=$SERVER_KEY", "Content-type:$CONTENT_TYPE")
        @POST("fcm/send")
        suspend fun postNotification(
                @Body notification:PushNotification
        ):Response<ResponseBody>
}