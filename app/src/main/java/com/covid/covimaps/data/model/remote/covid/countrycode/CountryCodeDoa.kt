package com.covid.covimaps.data.model.remote.covid.countrycode

import com.google.gson.annotations.SerializedName


data class Ara(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Bre(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class CapitalInfo(

    @SerializedName("latlng") var latlng: ArrayList<Double> = arrayListOf()

)

data class Car(

    @SerializedName("signs") var signs: ArrayList<String> = arrayListOf(),
    @SerializedName("side") var side: String? = null

)

data class Ces(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Currencies(

    @SerializedName("SHP") var SHP: SHP? = SHP()

)

data class Cym(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Demonyms(

    @SerializedName("eng") var eng: Eng? = Eng()

)

data class Deu(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Eng(

    @SerializedName("f") var f: String? = null,
    @SerializedName("m") var m: String? = null

)

data class Est(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class CountryCodes(

    @SerializedName("name") var name: Name? = Name(),
    /*@SerializedName("tld") var tld: ArrayList<String> = arrayListOf(),
    @SerializedName("cca2") var cca2: String? = null,
    @SerializedName("ccn3") var ccn3: String? = null,
    @SerializedName("cca3") var cca3: String? = null,
    @SerializedName("independent") var independent: Boolean? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("unMember") var unMember: Boolean? = null,
    @SerializedName("currencies") var currencies: Currencies? = Currencies(),*/
    @SerializedName("idd") var idd: Idd? = Idd(),
    @SerializedName("altSpellings") var altSpellings: ArrayList<String> = arrayListOf(),
    /*@SerializedName("capital") var capital: ArrayList<String> = arrayListOf(),
    @SerializedName("region") var region: String? = null,
    @SerializedName("languages") var languages: Languages? = Languages(),
    @SerializedName("translations") var translations: Translations? = Translations(),
    @SerializedName("latlng") var latlng: ArrayList<Double> = arrayListOf(),
    @SerializedName("landlocked") var landlocked: Boolean? = null,
    @SerializedName("area") var area: Int? = null,
    @SerializedName("demonyms") var demonyms: Demonyms? = Demonyms(),*/
    /*@SerializedName("flag") var flag: String? = null,
    @SerializedName("maps") var maps: Maps? = Maps(),
    @SerializedName("population") var population: Int? = null,
    @SerializedName("car") var car: Car? = Car(),
    @SerializedName("timezones") var timezones: ArrayList<String> = arrayListOf(),
    @SerializedName("continents") var continents: ArrayList<String> = arrayListOf(),*/
    @SerializedName("flags") var flags: Flags? = Flags()/*,
    @SerializedName("startOfWeek") var startOfWeek: String? = null,
    @SerializedName("capitalInfo") var capitalInfo: CapitalInfo? = CapitalInfo()*/

)

data class Fin(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Flags(

    @SerializedName("png") var png: String? = null,
    @SerializedName("svg") var svg: String? = null

)

data class Fra(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Hrv(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Hun(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Idd(

    @SerializedName("root") var root: String? = null,
    @SerializedName("suffixes") var suffixes: ArrayList<String> = arrayListOf()

)

data class Ita(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Jpn(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Kor(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Languages(

    @SerializedName("eng") var eng: String? = null

)

data class Maps(

    @SerializedName("googleMaps") var googleMaps: String? = null,
    @SerializedName("openStreetMaps") var openStreetMaps: String? = null

)

data class Name(

    @SerializedName("common") var common: String? = null,
    @SerializedName("official") var official: String? = null,
    @SerializedName("nativeName") var nativeName: NativeName? = NativeName()

)

data class NativeName(

    @SerializedName("eng") var eng: Eng? = Eng()

)

data class Nld(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Per(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Pol(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Por(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Rus(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class SHP(

    @SerializedName("name") var name: String? = null,
    @SerializedName("symbol") var symbol: String? = null

)

data class Slk(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Spa(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Srp(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Swe(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Translations(

    @SerializedName("ara") var ara: Ara? = Ara(),
    @SerializedName("bre") var bre: Bre? = Bre(),
    @SerializedName("ces") var ces: Ces? = Ces(),
    @SerializedName("cym") var cym: Cym? = Cym(),
    @SerializedName("deu") var deu: Deu? = Deu(),
    @SerializedName("est") var est: Est? = Est(),
    @SerializedName("fin") var fin: Fin? = Fin(),
    @SerializedName("fra") var fra: Fra? = Fra(),
    @SerializedName("hrv") var hrv: Hrv? = Hrv(),
    @SerializedName("hun") var hun: Hun? = Hun(),
    @SerializedName("ita") var ita: Ita? = Ita(),
    @SerializedName("jpn") var jpn: Jpn? = Jpn(),
    @SerializedName("kor") var kor: Kor? = Kor(),
    @SerializedName("nld") var nld: Nld? = Nld(),
    @SerializedName("per") var per: Per? = Per(),
    @SerializedName("pol") var pol: Pol? = Pol(),
    @SerializedName("por") var por: Por? = Por(),
    @SerializedName("rus") var rus: Rus? = Rus(),
    @SerializedName("slk") var slk: Slk? = Slk(),
    @SerializedName("spa") var spa: Spa? = Spa(),
    @SerializedName("srp") var srp: Srp? = Srp(),
    @SerializedName("swe") var swe: Swe? = Swe(),
    @SerializedName("tur") var tur: Tur? = Tur(),
    @SerializedName("urd") var urd: Urd? = Urd(),
    @SerializedName("zho") var zho: Zho? = Zho()

)

data class Tur(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Urd(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)

data class Zho(

    @SerializedName("official") var official: String? = null,
    @SerializedName("common") var common: String? = null

)