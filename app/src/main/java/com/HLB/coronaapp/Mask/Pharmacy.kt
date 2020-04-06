package com.HLB.coronaapp.Mask

import android.os.Parcel
import android.os.Parcelable

// Json 에서 읽어온 판매처에 대한 정보를 저장하는 Pharmacy 클래스
// FragmentMask 의 ArrayList<Pharmacy> 에서 받아야함.
class Pharmacy(val addr : String, val latitude : Double,
               val longitude : Double, val name : String,
               val remain_stat : String, val stock_at : String,
               val type : String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun toString(): String {
        return "$addr $latitude $longitude $name $remain_stat $stock_at"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addr)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(name)
        parcel.writeString(remain_stat)
        parcel.writeString(stock_at)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pharmacy> {
        override fun createFromParcel(parcel: Parcel): Pharmacy {
            return Pharmacy(parcel)
        }

        override fun newArray(size: Int): Array<Pharmacy?> {
            return arrayOfNulls(size)
        }
    }

}