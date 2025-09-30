package com.hrbabu.tracking.request_response.base
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
open class BaseResponse(

    @SerializedName("rs")
    val rs: Int? = 0,

    @SerializedName("msgkey")
    val msgkey: String = "",
)
