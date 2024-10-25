package com.portfolio.core.presentation.designsystem.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.portfolio.core.presentation.designsystem.R

@Composable
fun HogCameraPermissionRationaleDialog(
    onGrantPermission: () -> Unit,
    onDontGrantPermission: () -> Unit
) {
    HogDialog(
        title = stringResource(id = R.string.camera_permission_required),
        onDismiss = { /* Can't dismiss */ },
        description = stringResource(id = R.string.camera_permission_rationale),
        primaryButton = {
            Button(
                onClick = { onGrantPermission() }
            ) {
                Text(text = stringResource(id = R.string.grant_permission))
            }
        },
        secondaryButton = {
            Button(
                onClick = {
                    onDontGrantPermission()
                }) {
                Text(text = stringResource(id = R.string.dont_grant))
            }
        }
    )
}