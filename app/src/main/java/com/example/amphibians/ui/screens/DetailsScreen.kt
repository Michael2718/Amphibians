package com.example.amphibians.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.amphibians.R
import com.example.amphibians.model.Amphibian
import com.example.amphibians.ui.components.InfoCard

@Composable
fun DetailsScreen(
    amphibian: Amphibian,
    context: Context,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(scrollState)
    ) {
        InfoCard {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(amphibian.imgSrc)
                    .crossfade(true)
                    .build(),
                contentDescription = amphibian.name,
                modifier = Modifier.fillMaxWidth(),
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        InfoCard {
            Text(
                text = stringResource(
                    R.string.amphibian_card_title,
                    amphibian.name,
                    amphibian.type
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        InfoCard {
            Text(
                text = amphibian.description,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}