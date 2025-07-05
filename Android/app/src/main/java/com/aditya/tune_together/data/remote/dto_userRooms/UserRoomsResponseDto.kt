package com.aditya.tune_together.data.remote.dto_userRooms

import com.aditya.tune_together.data.remote.dto_register_user.SignInResponseDto
import com.aditya.tune_together.domain.model.sign_in.User
import com.aditya.tune_together.domain.model.user_rooms.Member
import com.aditya.tune_together.domain.model.user_rooms.UserRoom

data class UserRoomsResponseDto(
    val userRooms: List<UserRoom>
)



fun UserRoomsResponseDto.toUserRooms(): List<UserRoom> {
    return userRooms.map { dto ->
        UserRoom(
            hostEmail = dto.hostEmail,
            hostName = dto.hostName,
            isCreatedByYou = dto.isCreatedByYou,
            roomId = dto.roomId,
            roomName = dto.roomName,
            startTime = dto.startTime,
            members = dto.members.map { memberDto ->
                Member(
                    memberEmail = memberDto.memberEmail,
                    memberName = memberDto.memberName
                )
            }
        )
    }
}
