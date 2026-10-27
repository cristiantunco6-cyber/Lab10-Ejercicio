package com.tecsup.lab10ejercicio.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lab10ejercicio.data.ProductRequest
import com.tecsup.lab10ejercicio.viewmodel.ProductViewModel

// id = 0 crea un producto (POST), otro id edita el existente (PUT)
@Composable
fun ProductFormScreen(viewModel: ProductViewModel, id: Int, onVolver: () -> Unit) {
    val existente = remember(id) { viewModel.buscar(id) }
    var titulo by remember { mutableStateOf(existente?.title ?: "") }
    var precio by remember { mutableStateOf(existente?.price?.toString() ?: "") }
    var categoria by remember { mutableStateOf(existente?.category ?: "") }
    var descripcion by remember { mutableStateOf(existente?.description ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            if (id == 0) "Nuevo producto" else "Editar producto #$id",
            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Verde
        )
        Text(
            if (id == 0) "Se enviará POST /products/add" else "Se enviará PUT /products/$id",
            fontSize = 13.sp
        )
        OutlinedTextField(
            value = titulo, onValueChange = { titulo = it },
            label = { Text("Título") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio, onValueChange = { precio = it },
            label = { Text("Precio ($)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = categoria, onValueChange = { categoria = it },
            label = { Text("Categoría") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion, onValueChange = { descripcion = it },
            label = { Text("Descripción") }, minLines = 3, modifier = Modifier.fillMaxWidth()
        )
        error?.let { Text(it, color = androidx.compose.ui.graphics.Color.Red) }
        Button(
            onClick = {
                val precioNum = precio.toDoubleOrNull()
                if (titulo.isBlank() || precioNum == null) {
                    error = "Ingresa un título y un precio válido"
                    return@Button
                }
                val request = ProductRequest(titulo.trim(), descripcion.trim(), precioNum, categoria.trim())
                if (id == 0) viewModel.crear(request) else viewModel.actualizar(id, request)
                onVolver()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Verde),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(if (id == 0) "Crear producto (POST)" else "Guardar cambios (PUT)", fontSize = 16.sp)
        }
    }
}
