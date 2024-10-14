package com.portfolio.core.presentation.designsystem.components

// TODO: Delete if not needed
//@Composable
//fun HogTextFieldSingleLine(
//    state: TextFieldState,
////    startIcon: ImageVector?,
////    endIcon: ImageVector?,
////    hint: String,
//    title: String?,
//    modifier: Modifier = Modifier,
//    error: String? = null,
//    keyboardType: KeyboardType = KeyboardType.Text,
//    additionalInfo: String? = null
//) {
//    var isFocused by remember {
//        mutableStateOf(false)
//    }
//    val focusColor = MaterialTheme.colorScheme.primary
//    val unFocusColor = MaterialTheme.colorScheme.onSurface
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if(title != null) {
//                Text(
//                    text = title,
//                    color = unFocusColor,
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }
//            if(error != null) {
//                Text(
//                    text = error,
//                    color = MaterialTheme.colorScheme.error,
//                    fontSize = 12.sp
//                )
//            } else if(additionalInfo != null) {
//                Text(
//                    text = additionalInfo,
//                    color = unFocusColor,
//                    fontSize = 12.sp
//                )
//            }
//        }
//
//        BasicTextField(
//            state = state,
//            textStyle = LocalTextStyle.current.copy(
//                color = unFocusColor
//            ),
//            keyboardOptions = KeyboardOptions(
//                keyboardType = keyboardType
//            ),
//            lineLimits = TextFieldLineLimits.SingleLine,
//            cursorBrush = SolidColor(unFocusColor),
//            modifier = Modifier
//                .fillMaxWidth()
//                .defaultMinSize(minHeight = 55.dp)
//                .background(
//                    Color.Black.copy(alpha = 0f)
//                )
//                .border(
//                    width = 1.dp,
//                    color = if (isFocused) {
//                        focusColor
//                    } else {
//                        unFocusColor
//                    },
//                    shape = RoundedCornerShape(4.dp)
//                )
//                .padding(horizontal = 12.dp)
//                .onFocusChanged {
//                    isFocused = it.isFocused
//                },
//            decorator = {innerBox ->
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    innerBox()
//                }
//            }
//        )
//    }
//}
//
//
//@Composable
//fun HogTextFieldMultiLine(
//    state: TextFieldState,
////    startIcon: ImageVector?,
////    endIcon: ImageVector?,
////    hint: String,
//    title: String?,
//    modifier: Modifier = Modifier,
//    error: String? = null,
//    keyboardType: KeyboardType = KeyboardType.Text,
//    additionalInfo: String? = null
//) {
//    var isFocused by remember {
//        mutableStateOf(false)
//    }
//    val focusColor = MaterialTheme.colorScheme.primary
//    val unFocusColor = MaterialTheme.colorScheme.onSurface
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if(title != null) {
//                Text(
//                    text = title,
//                    color = unFocusColor,
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }
//            if(error != null) {
//                Text(
//                    text = error,
//                    color = MaterialTheme.colorScheme.error,
//                    fontSize = 12.sp
//                )
//            } else if(additionalInfo != null) {
//                Text(
//                    text = additionalInfo,
//                    color = unFocusColor,
//                    fontSize = 12.sp
//                )
//            }
//        }
//
//        BasicTextField(
//            state = state,
//            textStyle = LocalTextStyle.current.copy(
//                color = unFocusColor
//            ),
//            keyboardOptions = KeyboardOptions(
//                keyboardType = keyboardType
//            ),
//            lineLimits = TextFieldLineLimits.MultiLine(),
//            cursorBrush = SolidColor(unFocusColor),
//            modifier = Modifier
//                .fillMaxWidth()
//                .defaultMinSize(minHeight = 80.dp)
//                .background(
//                    Color.Black.copy(alpha = 0f)
//                )
//                .border(
//                    width = 1.dp,
//                    color = if (isFocused) {
//                        focusColor
//                    } else {
//                        unFocusColor
//                    },
//                    shape = RoundedCornerShape(4.dp)
//                )
//                .padding(horizontal = 12.dp)
//                .onFocusChanged {
//                    isFocused = it.isFocused
//                },
//            decorator = {innerBox ->
//                Box(modifier.padding(top = 8.dp)) {
//                    innerBox()
//                }
//            }
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun HogTextFieldPreview() {
//    RecipeHogTheme {
//        Surface {
//            HogTextFieldSingleLine(
//                state = rememberTextFieldState(),
//                title = "Title",
//                modifier = Modifier
//                    .fillMaxWidth(),
//                additionalInfo = "Must be a valid email"
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun HogTextFieldErrorPreview() {
//    RecipeHogTheme {
//        Surface {
//            HogTextFieldSingleLine(
//                state = rememberTextFieldState(),
//                title = "Title",
//                modifier = Modifier
//                    .fillMaxWidth(),
//                error = "Invalid title!",
//                additionalInfo = "Must be a valid email"
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun HogTextFieldNoInfoPreview() {
//    RecipeHogTheme {
//        Surface {
//            HogTextFieldSingleLine(
//                state = rememberTextFieldState(),
//                title = "Description",
//                modifier = Modifier
//                    .fillMaxWidth(),
//                additionalInfo = ""
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun HogTextFieldNoTitlePreview() {
//    RecipeHogTheme {
//        Surface {
//            HogTextFieldSingleLine(
//                state = rememberTextFieldState(),
//                title = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun HogTextFieldMultiLinePreview() {
//    RecipeHogTheme {
//        Surface {
//            HogTextFieldMultiLine(
//                state = rememberTextFieldState(),
//                title = "Description",
//                modifier = Modifier
//                    .fillMaxWidth(),
//                additionalInfo = ""
//            )
//        }
//    }
//}