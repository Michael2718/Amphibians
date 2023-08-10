package com.example.amphibians.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.amphibians.R
import com.example.amphibians.model.Amphibian
import com.example.amphibians.ui.RequestStatus

@Composable
fun HomeScreen(
    requestStatus: RequestStatus,
    retryAction: () -> Unit,
    onItemClick: (Amphibian) -> Unit,
    modifier: Modifier = Modifier
) {
    when (requestStatus) {
        is RequestStatus.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize()
        )

        is RequestStatus.Success -> AmphibiansListScreen(
            amphibians = requestStatus.amphibians,
            onItemClick = onItemClick,
            modifier = modifier.fillMaxWidth()
        )

        is RequestStatus.Error -> ErrorScreen(
            retryAction = retryAction,
            modifier = modifier.fillMaxSize()
        )
    }
//    AmphibiansList(
//        amphibians = listOf(
//            Amphibian(
//                name = "Great Basin Spadefoot",
//                type = "Toad",
//                description = "This toad spends most of its life underground due to the arid desert conditions in which it lives. Spadefoot toads earn the name because of their hind legs which are wedged to aid in digging. They are typically grey, green, or brown with dark spots.",
//                imgSrc = "https://developer.android.com/codelabs/basic-android-kotlin-compose-amphibians-app/img/great-basin-spadefoot.png"
//            ),
//            Amphibian(
//                name = "Roraima Bush Toad",
//                type = "Toad",
//                description = "This toad is typically found in South America. Specifically on Mount Roraima at the boarders of Venezuala, Brazil, and Guyana, hence the name. The Roraiam Bush Toad is typically black with yellow spots or marbling along the throat and belly.",
//                imgSrc = "https://developer.android.com/codelabs/basic-android-kotlin-compose-amphibians-app/img/roraima-bush-toad.png"
//            )
//        ),
//        modifier = modifier
//    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading),
        modifier = modifier.size(dimensionResource(R.dimen.loading_image_size))
    )
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_connection_error),
            contentDescription = stringResource(R.string.connection_error)
        )
        Text(
            text = stringResource(R.string.loading_failed),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        )
        Button(onClick = retryAction) {
            Text(text = stringResource(R.string.retry))
        }
    }
}


@Composable
fun AmphibiansListScreen(
    amphibians: List<Amphibian>,
    onItemClick: (Amphibian) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = amphibians, key = { amphibia -> amphibia.name }) { amphibia ->
            AmphibiaCard(
                amphibian = amphibia,
                onItemClick,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmphibiaCard(
    amphibian: Amphibian,
    onItemClick: (Amphibian) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onItemClick(amphibian) },
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
//            modifier = Modifier.padding(4.dp)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val title = "${amphibian.name} (${amphibian.type})"
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge
            )
//            Box {
//                AsyncImage(
//                    model = ImageRequest.Builder(context = LocalContext.current)
//                        .data(amphibia.imgSrc)
//                        .crossfade(true)
//                        .build(),
//                    contentDescription = amphibia.name,
//                    modifier = Modifier.fillMaxWidth(),
//                    placeholder = painterResource(R.drawable.loading_img),
//                    error = painterResource(R.drawable.ic_broken_image),
//                    contentScale = ContentScale.Fit
//                )
//            }

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(amphibian.imgSrc)
                    .crossfade(true)
                    .build(),
                contentDescription = amphibian.name,
                modifier = Modifier.fillMaxWidth(),
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Crop
            )
//            Image(
//                painter = painterResource(R.drawable.great_basin_spadefoot),
//                contentDescription = amphibian.name,
//                modifier = Modifier.fillMaxWidth(),
//                contentScale = ContentScale.Crop
//            )
            Text(
                text = amphibian.description,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AmphibiaCardPreview() {
//    AmphibiansTheme {
//        val toad = Amphibia(
//            name = "Great Basin Spadefoot",
//            type = "Toad",
//            description = "This toad is typically found in South America. Specifically on Mount Roraima at the boarders of Venezuala, Brazil, and Guyana, hence the name. The Roraiam Bush Toad is typically black with yellow spots or marbling along the throat and belly.",
//            imgSrc = "https://developer.android.com/codelabs/basic-android-kotlin-compose-amphibians-app/img/roraima-bush-toad.png"
//        )
//        AmphibiaCard(
//            amphibia = toad,
//            {}
//        )
//    }
//}
