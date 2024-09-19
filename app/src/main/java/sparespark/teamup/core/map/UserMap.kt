package sparespark.teamup.core.map

import com.google.firebase.auth.FirebaseUser
import sparespark.teamup.core.ROLE_ADMIN
import sparespark.teamup.core.ROLE_EMPLOYEE
import sparespark.teamup.core.ROLE_OWNER
import sparespark.teamup.data.model.RemoteUser
import sparespark.teamup.data.model.User
import sparespark.teamup.data.room.user.RoomUser

internal const val DEACTIVATED = false

private const val DEF_NAME = "userX"
private const val DEF_EMAIL = "userX@teamup.com"

private fun String.isOwnerEmail(): Boolean = this == "owner@" || this == "m7md7lwa@gmail.com"
private fun String.isAdminEmail(): Boolean = this == "admin@" || this == "test@teamup.com"

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