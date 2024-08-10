package com.example.myapplication.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ResponseFollowers(

	@field:SerializedName("ResponseFollowers")
	val responseFollowers: List<ResponseFollowersItem?>? = null
)

data class ResponseFollowersItem(

	@field:SerializedName("gists_url")
	val gistsUrl: String? = null,

	@field:SerializedName("repos_url")
	val reposUrl: String? = null,

	@field:SerializedName("following_url")
	val followingUrl: String? = null,

	@field:SerializedName("starred_url")
	val starredUrl: String? = null,

	@field:SerializedName("login")
	val login: String? = null,

	@field:SerializedName("followers_url")
	val followersUrl: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("subscriptions_url")
	val subscriptionsUrl: String? = null,

	@field:SerializedName("received_events_url")
	val receivedEventsUrl: String? = null,

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null,

	@field:SerializedName("events_url")
	val eventsUrl: String? = null,

	@field:SerializedName("html_url")
	val htmlUrl: String? = null,

	@field:SerializedName("site_admin")
	val siteAdmin: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("node_id")
	val nodeId: String? = null,
	
	@field:SerializedName("followers")
	val followersCount: Int? = null,

	@field:SerializedName("following")
	val followingCount: Int? = null,

	var isFavorite: Boolean = false

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(gistsUrl)
		parcel.writeString(reposUrl)
		parcel.writeString(followingUrl)
		parcel.writeString(starredUrl)
		parcel.writeString(login)
		parcel.writeString(followersUrl)
		parcel.writeString(type)
		parcel.writeString(url)
		parcel.writeString(subscriptionsUrl)
		parcel.writeString(receivedEventsUrl)
		parcel.writeString(avatarUrl)
		parcel.writeString(eventsUrl)
		parcel.writeString(htmlUrl)
		parcel.writeValue(siteAdmin)
		parcel.writeValue(id)
		parcel.writeString(nodeId)

		parcel.writeValue(followersCount)
		parcel.writeValue(followingCount)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ResponseFollowersItem> {
		override fun createFromParcel(parcel: Parcel): ResponseFollowersItem {
			return ResponseFollowersItem(parcel)
		}

		override fun newArray(size: Int): Array<ResponseFollowersItem?> {
			return arrayOfNulls(size)
		}
	}

}
