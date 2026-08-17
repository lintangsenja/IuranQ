package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Custom clean livestock & sacrificial saving icon (Siluet ternak sapi/kambing kurban)
 * Digunakan sebagai pengganti ikon tapak kaki (Pets)
 */
val Icons.Filled.LivestockKurban: ImageVector
    get() {
        if (_livestockKurban != null) {
            return _livestockKurban!!
        }
        _livestockKurban = materialIcon(name = "Filled.LivestockKurban") {
            // Body silhouette of healthy livestock (sacrificial cow/sheep/goat)
            materialPath {
                moveTo(20.0f, 7.5f)
                curveTo(19.2f, 6.6f, 18.0f, 6.0f, 16.8f, 6.0f)
                curveTo(15.7f, 6.0f, 14.7f, 6.5f, 14.0f, 7.2f)
                curveTo(13.2f, 5.5f, 11.8f, 4.5f, 10.0f, 4.5f)
                curveTo(7.2f, 4.5f, 5.0f, 6.7f, 5.0f, 9.5f)
                curveTo(5.0f, 10.7f, 5.4f, 11.8f, 6.1f, 12.6f)
                lineTo(4.5f, 19.0f)
                horizontalLineTo(7.0f)
                lineToRelative(1.0f, -3.5f)
                horizontalLineToRelative(6.0f)
                lineToRelative(1.0f, 3.5f)
                horizontalLineToRelative(2.5f)
                lineToRelative(-1.6f, -6.0f)
                curveTo(18.2f, 11.8f, 20.0f, 9.8f, 20.0f, 7.5f)
                close()
                // Horn / Ear details
                moveTo(4.0f, 4.0f)
                lineTo(6.5f, 6.5f)
                lineTo(5.0f, 8.0f)
                close()
                moveTo(18.0f, 4.0f)
                lineTo(19.5f, 6.5f)
                lineTo(17.5f, 7.5f)
                close()
            }
        }
        return _livestockKurban!!
    }

val LivestockKurban: ImageVector
    get() = Icons.Filled.LivestockKurban

private var _livestockKurban: ImageVector? = null
