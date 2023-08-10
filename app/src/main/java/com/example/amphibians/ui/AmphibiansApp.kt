package com.example.amphibians.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.amphibians.R
import com.example.amphibians.ui.screens.DetailsScreen
import com.example.amphibians.ui.screens.HomeScreen

enum class AmphibiansScreen {
    Start,
    Details
}

@Composable
fun AmphibiansApp(
    modifier: Modifier = Modifier,
    viewModel: AmphibiansViewModel = viewModel(factory = AmphibiansViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AmphibiansScreen.valueOf(
        backStackEntry?.destination?.route ?: AmphibiansScreen.Start.name
    )
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AmphibiansTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AmphibiansScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(AmphibiansScreen.Start.name) {
                HomeScreen(
                    requestStatus = uiState.requestStatus,
                    context = context,
                    retryAction = viewModel::getAmphibians,
                    onItemClick = {
                        navController.navigate(AmphibiansScreen.Details.name)
                        viewModel.updateCurrentAmphibian(it)
                    }
                )
            }
            composable(AmphibiansScreen.Details.name) {
                DetailsScreen(
                    amphibian = uiState.currentAmphibian!!,
                    context = context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmphibiansTopAppBar(
    currentScreen: AmphibiansScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    )
}