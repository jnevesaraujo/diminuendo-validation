package com.example.damfp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.damfp.ui.feature.detail.DetailScreen
import com.example.damfp.ui.feature.sample.SampleRoute

/** App routes. Document the graph in docs/05_navigation.md. */
object Routes {
    const val SAMPLE = "sample"
    const val DETAIL = "detail/{id}"

    fun detail(id: String) = "detail/$id"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SAMPLE) {
        composable(Routes.SAMPLE) {
            SampleRoute(
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { entry ->
            DetailScreen(
                id = entry.arguments?.getString("id").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
