package com.github.blockchain.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by blockchain on 19/04/18.
 */
data class Apk(val appName: String, val sourceDir: String, val packageName: String? = "", val version: String? = "", val systemApp: Boolean, val publicKey: String? = "" ) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appName)
        parcel.writeString(sourceDir)
        parcel.writeString(packageName)
        parcel.writeString(version)
        parcel.writeByte(if (systemApp) 1 else 0)
        parcel.writeString(publicKey)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Apk> {
        override fun createFromParcel(parcel: Parcel): Apk = Apk(parcel)
        override fun newArray(size: Int): Array<Apk?> = arrayOfNulls(size)
    }
}