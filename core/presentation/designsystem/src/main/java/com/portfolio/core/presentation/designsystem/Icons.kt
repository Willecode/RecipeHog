package com.portfolio.core.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

val CheckIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.baseline_check_24)

val CrossIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.baseline_close_24)

val EyeClosedIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.outline_visibility_off_24)

val EyeOpenedIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.outline_visibility_24)

val EmailIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.outline_email_24)

val LockIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.outline_lock_24)

val LogoutIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.outline_logout_24)

val PersonIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.outline_person_24)