package com.example.almacercaapp.ui.theme.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.almacercaapp.ui.theme.GreenPrimary

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)

val chatMessages = listOf(
    ChatMessage("Hola, Buenos días.", false, "10:41 pm"),
    ChatMessage("Soy Mercadop de Soporte, ¿En qué puedo ayudarte?", false, "10:41 pm"),
    ChatMessage("Hola, estoy teniendo problemas con mi pedido y con el proceso de pago.", true, "10:41 pm"),
    ChatMessage("¿Me podrías ayudar?", true, "10:41 pm"),
    ChatMessage("Claro..", false, "10:50 pm"),
    ChatMessage("¿Me podrías decir el problema en específico que tienes? Así puedo brindarte ayuda.", false, "10:51 pm")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatScreen(navController: NavController) {
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* llamar */ }) {
                        Icon(Icons.Default.Phone, contentDescription = "Llamar")
                    }
                }
            )
        },
        bottomBar = {
            // Barra de entrada de chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        Icon(Icons.Default.Mic, contentDescription = "Voz")
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { /* enviar mensaje */ },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = GreenPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // "Hoy"
            Text(
                text = "Hoy",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                reverseLayout = true
            ) {

                items(chatMessages.reversed()) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 0.dp,
                bottomEnd = if (message.isFromUser) 0.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) GreenPrimary else Color(0xFFF0F0F0)
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isFromUser) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = message.timestamp,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}