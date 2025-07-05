package com.aditya.tune_together.data.remote.dto_room

import com.aditya.tune_together.domain.model.room.Member
import com.aditya.tune_together.domain.model.room.Room
import com.aditya.tune_together.domain.model.room.Song

data class RoomDto(
    val room: RoomDetails,
)

fun RoomDto.toRoom(): Room {
    return Room(
        hostEmail = room.hostEmail,
        hostFcmToken = room.hostFcmToken,
        hostId = room.hostId,
        hostName = room.hostName,
        members = room.members.map { memberDto ->
            Member(
                name = memberDto.name,
                email = memberDto.email,
                fcmToken = memberDto.fcmToken
            )
        },
        roomName = room.roomName,
        songs = room.songs.map { songDto ->
            Song(
                name = songDto.name,
                url = songDto.url
            )
        },
        startTime = room.startTime

    )

}
