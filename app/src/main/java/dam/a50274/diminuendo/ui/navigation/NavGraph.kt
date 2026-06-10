package dam.a50274.diminuendo.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dam.a50274.diminuendo.ui.feature.ai.AiConsultantScreen
import dam.a50274.diminuendo.ui.feature.auth.AuthScreenRoot
import dam.a50274.diminuendo.ui.feature.capture.CaptureScreenRoot
import dam.a50274.diminuendo.ui.feature.diary.DiaryScreenRoot
import dam.a50274.diminuendo.ui.feature.heatmap.HeatmapScreen
import dam.a50274.diminuendo.ui.feature.paywall.PaywallScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier, startDestination: Any = Auth) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Auth> {
            AuthScreenRoot(
                onNavigateToHome = {
                    navController.navigate(Heatmap) {
                        popUpTo(Auth) { inclusive = true }
                    }
                },
            )
        }
        composable<Heatmap> {
            HeatmapScreen()
        }
        composable<Capture>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "diminuendo://capture"
                    action = Intent.ACTION_VIEW
                },
            ),
        ) {
            CaptureScreenRoot()
        }
        composable<Diary> {
            DiaryScreenRoot()
        }
        composable<AiConsultant> {
            AiConsultantScreen()
        }
        composable<Paywall> {
            PaywallScreen()
        }
    }
}
