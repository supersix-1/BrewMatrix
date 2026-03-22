package com.brewmatrix.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val BrewMatrixShapes = Shapes(
    small = RoundedCornerShape(12.dp),   // Input fields
    medium = RoundedCornerShape(16.dp),  // Cards, large buttons
    large = RoundedCornerShape(24.dp),   // Chips/pills, bottom sheet top corners
)

// Named shape constants for explicit usage
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(16.dp)
val ChipShape = RoundedCornerShape(24.dp)
val InputFieldShape = RoundedCornerShape(12.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val FabShape = RoundedCornerShape(20.dp)
