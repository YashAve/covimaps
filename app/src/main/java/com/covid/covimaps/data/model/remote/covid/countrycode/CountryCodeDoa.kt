package com.covid.covimaps.data.model.remote.covid.countrycode

import com.google.gson.annotations.SerializedName

data class Eng(

    @SerializedName("f") var f: String? = null,
    @SerializedName("m") var m: String? = null

)

data class CountryCodes(

    @SerializedName("name") var name: Name? = Name(),
    @SerializedName("idd") var idd: Idd? = Idd(),
    @SerializedName("altSpellings") var altSpellings: ArrayList<String> = arrayListOf(),
    @SerializedName("flags") var flags: Flags? = Flags()
)

data class Flags(

    @SerializedName("png") var png: String? = null,
    @SerializedName("svg") var svg: String? = null

)

data class Idd(

    @SerializedName("root") var root: String? = null,
    @SerializedName("suffixes") var suffixes: ArrayList<String> = arrayListOf()

)

data class Name(

    @SerializedName("common") var common: String? = null,
    @SerializedName("official") var official: String? = null,
    @SerializedName("nativeName") var nativeName: NativeName? = NativeName()

)

data class NativeName(

    @SerializedName("eng") var eng: Eng? = Eng()

)