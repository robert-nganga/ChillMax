package com.example.chillmax.presentation.details.components

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.chillmax.R
import com.example.chillmax.domain.models.responses.CastDetailsApiResponse
import com.example.chillmax.presentation.ui.theme.*
import com.example.chillmax.util.Resource
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun DetailsContent(
    navigator: DestinationsNavigator,
    filmName: String,
    posterUrl: String,
    releaseDate: String,
    overview: String,
    casts: Resource<CastDetailsApiResponse>,
    state: LazyListState
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    val currentSheetFraction = scaffoldState.currentSheetFraction

    val radiusAnim by animateDpAsState(
        targetValue =
        if (currentSheetFraction ==1f)
            EXTRA_LARGE_PADDING
        else RADIUS_DP)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(
            topStart = radiusAnim,
            topEnd = radiusAnim
        ),
        sheetPeekHeight = 140.dp,
        sheetContent = {
                    MovieBottomSheetContent(
                        releaseDate = releaseDate,
                        overview =overview,
                        filmName = filmName,
                        casts = casts,
                        state = state
                    )
        },
        content = {
            MovieBackgroundColorSpan(
                posterUrl = posterUrl,
                imageFraction = currentSheetFraction,
                onCloseClick = {
                    navigator.popBackStack()
                }
            )
        }
    )
}

@ExperimentalCoilApi
@Composable
fun MovieBottomSheetContent(
    releaseDate: String,
    overview: String,
    filmName: String,
    casts: Resource<CastDetailsApiResponse>,
    sheetColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = Color.LightGray,
    state: LazyListState
) {
    Column(
        modifier = Modifier
            .background(sheetColor)
            .padding(all = SHEET_PADDING)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filmName,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
        }
        Spacer(modifier = Modifier.height(SMALL_PADDING))
        Text(
            text = stringResource(R.string.release_date),
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(EXTRA_SMALL_PADDING))
        Text(
            text = releaseDate,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.height(EXTRA_SMALL_PADDING))
        Text(
            text = stringResource(R.string.synopsis),
            color = contentColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.height(EXTRA_SMALL_PADDING))
        Text(
            text = overview,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(EXTRA_SMALL_PADDING))
        if(casts is Resource.Success){
            CastDetails(casts = casts.data!!, scrollState = state)
        }

    }
}
@ExperimentalCoilApi
@Composable
fun MovieBackgroundColorSpan(
    posterUrl: String,
    imageFraction: Float = 1f,
    backgroundColor: Color = MaterialTheme.colors.surface,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)

    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = imageFraction + 0.4f)
                .align(Alignment.TopStart),
            painter = rememberImagePainter(
                data = posterUrl,
                builder = {
                    placeholder(R.drawable.ic_placeholder)
                    crossfade(true)
                }
            ),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(R.string.background_image)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { onCloseClick() })
            {
                Icon(
                    modifier = Modifier.size(INFO_ICON_SIZE),
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_button),
                    tint = Color.White
                )
            }
        }
    }
}
@ExperimentalMaterialApi
val BottomSheetScaffoldState.currentSheetFraction:Float
    get() {
        val fraction = bottomSheetState.progress.fraction
        val targetValue = bottomSheetState.targetValue
        val currentValue = bottomSheetState.currentValue

        Log.d("Fraction", fraction.toString())
        Log.d("Target", targetValue.toString())
        Log.d("Current", currentValue.toString())

        return when{
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed ->1f
            currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded ->0f
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Expanded ->1f - fraction
            currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Collapsed ->1f + fraction
            else -> fraction
        }
    }