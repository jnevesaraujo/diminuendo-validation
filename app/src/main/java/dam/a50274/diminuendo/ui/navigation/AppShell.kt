package dam.a50274.diminuendo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dam.a50274.diminuendo.R

@Composable
fun AppShell(startDestination: Any = Auth) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute<Heatmap>() || it.hasRoute<Capture>() ||
            it.hasRoute<Diary>() || it.hasRoute<AiConsultant>()
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Place, contentDescription = stringResource(R.string.nav_heatmap)) },
                        label = { Text(stringResource(R.string.nav_heatmap)) },
                        selected = currentDestination.hierarchy.any { it.hasRoute<Heatmap>() },
                        onClick = {
                            navController.navigate(Heatmap) {
                                popUpTo(Heatmap) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = stringResource(R.string.nav_capture),
                            )
                        },
                        label = { Text(stringResource(R.string.nav_capture)) },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Capture>() } == true,
                        onClick = {
                            navController.navigate(Capture) {
                                popUpTo(Heatmap) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.nav_diary),
                            )
                        },
                        label = { Text(stringResource(R.string.nav_diary)) },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Diary>() } == true,
                        onClick = {
                            navController.navigate(Diary) {
                                popUpTo(Heatmap) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = stringResource(R.string.nav_ai_consultant),
                            )
                        },
                        label = { Text(stringResource(R.string.nav_ai)) },
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<AiConsultant>() } == true,
                        onClick = {
                            navController.navigate(AiConsultant) {
                                popUpTo(Heatmap) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = startDestination,
        )
    }
}
