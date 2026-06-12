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
import dam.a50274.diminuendo.ui.feature.profile.ProfileScreenRoot

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
            HeatmapScreen(
                onNavigateToPaywall = { navController.navigate(Paywall) },
                onNavigateToProfile = { navController.navigate(Profile) },
            )
        }
        composable<Capture>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "diminuendo://capture"
                    action = Intent.ACTION_VIEW
                },
            ),
        ) {
            CaptureScreenRoot(
                onNavigateToProfile = { navController.navigate(Profile) },
                onNavigateToDiary = {
                    navController.navigate(Diary) {
                        popUpTo(Heatmap) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable<Diary> {
            DiaryScreenRoot(
                onNavigateToProfile = { navController.navigate(Profile) },
            )
        }
        composable<AiConsultant> {
            AiConsultantScreen(
                onNavigateToPaywall = { navController.navigate(Paywall) },
                onNavigateToProfile = { navController.navigate(Profile) },
            )
        }
        composable<Paywall> {
            PaywallScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable<Profile> {
            ProfileScreenRoot(
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    // Navigate to Auth
                    navController.navigate(Auth) {
                        // Cleans all screens to prevent backwards navigation
                        popUpTo<Profile> { inclusive = true }
                    }
                }
            )
        }
    }
}
