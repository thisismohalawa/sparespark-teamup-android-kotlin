package sparespark.teamup.core.internal

import com.google.firebase.auth.FirebaseUser
import sparespark.teamup.data.model.user.RemoteUser
import sparespark.teamup.data.model.user.User
import sparespark.teamup.data.room.user.RoomUser


private const val DEF_NAME = "default"
private const val DEF_EMAIL = "default@teamup.com"

private fun String.isOwnerEmail(): Boolean =
    this == "ow1@gmail.com" || this == "ow2@gmail.com"

private fun String.isAdminEmail(): Boolean =
    this == "ad1@gmail.com" || this == "ad2@teamup.com"

internal val FirebaseUser.toUser: User
    get() = User(
        uid = this.uid,
        name = this.displayName ?: DEF_NAME,
        email = this.email ?: DEF_EMAIL,
        phone = this.phoneNumber ?: "",
        roleId = if (this.email?.isOwnerEmail() == true) ROLE_OWNER
        else if (this.email?.isAdminEmail() == true) ROLE_ADMIN else ROLE_EMPLOYEE,
        activated = if (this.email?.isOwnerEmail() == true ||
            this.email?.isAdminEmail() == true
        ) true else DEACTIVATED
    )


internal val RemoteUser.toUser: User
    get() = User(
        uid = this.uid ?: "",
        name = this.name ?: DEF_NAME,
        email = this.email ?: DEF_EMAIL,
        phone = this.phone ?: "",
        roleId = this.roleId ?: ROLE_EMPLOYEE,
        activated = this.activated ?: DEACTIVATED
    )
internal val RoomUser.toUser: User
    get() = User(
        uid = this.uid,
        name = this.name,
        email = this.email,
        phone = this.phone,
        roleId = this.roleId,
        activated = this.activated
    )
internal val User.toRoomUser: RoomUser
    get() = RoomUser(
        uid = this.uid,
        name = this.name,
        email = this.email,
        phone = this.phone,
        roleId = this.roleId,
        activated = this.activated
    )