package com.hrbabu.tracking.request_response.getResponse

import com.google.gson.annotations.SerializedName
import com.hrbabu.tracking.request_response.base.BaseResponse

data class GetResponse(

    @field:SerializedName("res")
    val res: Res? = null,

    @field:SerializedName("rc")
    val rc: List<RcItem?>? = null
) : BaseResponse()

data class Res(

    @field:SerializedName("msgKey")
    val msgKey: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("typeOfMsg")
    val typeOfMsg: Any? = null,

    @field:SerializedName("msgInHindi")
    val msgInHindi: Any? = null,

    @field:SerializedName("msgCode")
    val msgCode: Int? = null,

    @field:SerializedName("msgInEng")
    val msgInEng: Any? = null
)

data class RcItem(

    @field:SerializedName("msgKey")
    val msgKey: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("typeOfMsg")
    val typeOfMsg: Boolean? = null,

    @field:SerializedName("msgInHindi")
    val msgInHindi: String? = null,

    @field:SerializedName("msgCode")
    val msgCode: Int? = null,

    @field:SerializedName("msgInEng")
    val msgInEng: String? = null
)
