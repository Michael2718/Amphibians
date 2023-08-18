package com.example.amphibians.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.amphibians.R
import com.example.amphibians.model.Amphibian
import com.example.amphibians.ui.screens.DetailsScreen
import com.example.amphibians.ui.screens.HomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class AmphibiansScreen {
    Start,
    Details
}

@OptIn(ExperimentalMaterial3Api::class)
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AmphibiansTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                scrollBehavior = scrollBehavior,
                navigateUp = { navController.navigateUp() },
                onShareButtonClicked = {
                    viewModel.viewModelScope.launch {
                        shareAmphibian(
                            intentContext = context,
                            amphibian = uiState.currentAmphibian!!
                        )
                    }
                },
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
    scrollBehavior: TopAppBarScrollBehavior,
    navigateUp: () -> Unit,
    onShareButtonClicked: () -> Unit,
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
        },
        actions = {
            if (currentScreen == AmphibiansScreen.Details) {
                IconButton(onClick = onShareButtonClicked) {
                    Icon(imageVector = Icons.Filled.Share, stringResource(R.string.share))
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

private suspend fun shareAmphibian(
    intentContext: Context,
    amphibian: Amphibian
) {
    val imageFile = downloadImageFromUrl(
        context = intentContext,
        url = amphibian.imgSrc
    )
    val imageUri = FileProvider.getUriForFile(
        intentContext,
        "${intentContext.packageName}.fileprovider",
        imageFile
    )

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "image/png"
        putExtra(
            Intent.EXTRA_STREAM,
            imageUri
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    intentContext.startActivity(Intent.createChooser(shareIntent, "Share ${amphibian.name}'s photo!"))
}

private suspend fun downloadImageFromUrl(
    context: Context,
    url: String
): File {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .build()

    val drawable = withContext(Dispatchers.IO) {
        loader.execute(request).drawable
    }
    val bitmap = (drawable as BitmapDrawable).bitmap

    val imageFile = File(context.cacheDir, "photo.png")
    val outputStream = withContext(Dispatchers.IO) {
        FileOutputStream(imageFile)
    }
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

    withContext(Dispatchers.IO) {
        outputStream.flush()
    }

    return imageFile
}
