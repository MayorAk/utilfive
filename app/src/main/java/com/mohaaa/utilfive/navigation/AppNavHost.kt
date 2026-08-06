package com.mohaaa.utilfive.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohaaa.utilfive.ui.calculator.CalculatorScreen
import com.mohaaa.utilfive.ui.converter.ConverterScreen
import com.mohaaa.utilfive.ui.filemanager.FileManagerScreen
import com.mohaaa.utilfive.ui.flashlight.FlashlightScreen
import com.mohaaa.utilfive.ui.home.HomeScreen
import com.mohaaa.utilfive.ui.qrscanner.QrScannerScreen
import com.mohaaa.utilfive.ui.soundmeter.SoundMeterScreen

object Routes {
    const val HOME = "home"
    const val FLASHLIGHT = "flashlight"
    const val QR_SCANNER = "qr_scanner"
    const val CONVERTER = "converter"
    const val CALCULATOR = "calculator"
    const val SOUND_METER = "sound_meter"
    const val FILE_MANAGER = "file_manager"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Routes.FLASHLIGHT) {
            FlashlightScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.QR_SCANNER) {
            QrScannerScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CONVERTER) {
            ConverterScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.CALCULATOR) {
            CalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SOUND_METER) {
            SoundMeterScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.FILE_MANAGER) {
            FileManagerScreen(onBack = { navController.popBackStack() })
        }
    }
}
