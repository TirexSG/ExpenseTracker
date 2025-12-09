package com.tirexdev.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tirexdev.expensetracker.ui.editor.ExpenseEditorScreen
import com.tirexdev.expensetracker.ui.home.HomeScreen
import com.tirexdev.expensetracker.ui.statistics.StatisticsScreen

@Composable
fun ExpenseNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {
        composable<Routes.Home> {
            HomeScreen(
                onNavigateToAddExpense = {
                    navController.navigate(Routes.ExpenseEditor())
                },
                onNavigateToEditExpense = { expenseId ->
                    navController.navigate(Routes.ExpenseEditor(expenseId))
                }
            )
        }

        composable<Routes.Statistics> {
            StatisticsScreen()
        }

        composable<Routes.ExpenseEditor> { backStackEntry ->
            val args: Routes.ExpenseEditor = backStackEntry.toRoute()
            ExpenseEditorScreen(
                expenseId = args.expenseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}