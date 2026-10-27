package com.tecsup.lab10ejercicio.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tecsup.lab10ejercicio.data.ProductModel
import com.tecsup.lab10ejercicio.viewmodel.ProductViewModel

val Verde = Color(0xFF00695C)
val Naranja = Color(0xFFFF8F00)

@Composable
fun ProductListScreen(viewModel: ProductViewModel, onEditar: (Int) -> Unit) {
    val estado = viewModel.uiState
    var aEliminar by remember { mutableStateOf<ProductModel?>(null) }
    val listState = rememberLazyListState()

    // Al crear un producto se agrega al inicio, se desplaza la lista para que se vea
    val primerId = estado.productos.firstOrNull()?.id
    LaunchedEffect(primerId) {
        if (primerId != null) listState.animateScrollToItem(0)
    }

    when {
        estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Verde)
        }
        estado.error != null -> Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(estado.error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { viewModel.cargar() }) { Text("Reintentar") }
        }
        else -> LazyColumn(
            state = listState,
            contentPadding = PaddingValues(12.dp, 12.dp, 12.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(estado.productos, key = { it.id }) { producto ->
                ProductCard(
                    producto = producto,
                    onEditar = { onEditar(producto.id) },
                    onEliminar = { aEliminar = producto }
                )
            }
        }
    }

    aEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que deseas eliminar \"${producto.title}\"? Se enviará DELETE /products/${producto.id}") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminar(producto.id)
                        aEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { aEliminar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun ProductCard(producto: ProductModel, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditar() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (producto.thumbnail != null) {
                AsyncImage(
                    model = producto.thumbnail,
                    contentDescription = producto.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(84.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F1F1))
                )
            } else {
                Box(
                    Modifier.size(84.dp).clip(RoundedCornerShape(12.dp)).background(Verde.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Verde, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    producto.title, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Text(
                    (producto.category ?: "").uppercase(),
                    fontSize = 11.sp, color = Verde, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$ ${"%.2f".format(producto.price)}", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    producto.rating?.let {
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Naranja, modifier = Modifier.size(16.dp))
                        Text(" $it", fontSize = 13.sp)
                    }
                }
                Text("ID ${producto.id}", fontSize = 11.sp, color = Color.Gray)
            }
            Column {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = Verde)
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
