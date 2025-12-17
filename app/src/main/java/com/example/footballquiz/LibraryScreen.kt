package com.example.footballquiz

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

data class LibraryItem(
    val topic: String,
    val term: String,
    val description: String,
    val example: String? = null
)

private object LibraryData {
    val topics = listOf("Правила", "Тактика", "Позиции", "Турниры", "Сленг")

    val items: List<LibraryItem> = listOf(
        LibraryItem(
            "Правила",
            "Офсайд",
            "Положение вне игры: игрок атакующей команды ближе к воротам соперника, чем мяч и предпоследний защитник в момент передачи.",
            "Судья фиксирует офсайд, если игрок влияет на эпизод."
        ),
        LibraryItem("Правила", "Пенальти", "Удар с 11 метров за нарушение в штрафной площади.", "Фол рукой защитника в штрафной → пенальти."),
        LibraryItem("Тактика", "Прессинг", "Активное давление на соперника с целью быстро вернуть мяч.", "Высокий прессинг — давление у штрафной соперника."),
        LibraryItem("Тактика", "Контратака", "Быстрая атака сразу после отбора мяча.", "Отбор → 2-3 передачи → удар."),
        LibraryItem("Позиции", "Центрбек", "Центральный защитник — играет в центре обороны, читает игру и страхует.", null),
        LibraryItem("Позиции", "Плеймейкер", "Игрок, который создаёт атаки: видение поля, пас, темп.", null),
        LibraryItem("Турниры", "Лига чемпионов", "Главный клубный турнир Европы под эгидой UEFA.", null),
        LibraryItem("Сленг", "Хет-трик", "Три гола одного игрока в одном матче.", "Иногда выделяют “идеальный хет-трик”: левой, правой и головой.")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf<String?>(null) }

    val filtered = remember(query, selectedTopic) {
        LibraryData.items
            .asSequence()
            .filter { selectedTopic == null || it.topic == selectedTopic }
            .filter {
                query.isBlank() ||
                        it.term.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        (it.example?.contains(query, ignoreCase = true) == true)
            }
            .toList()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Библиотека", canBack = true, onBack = onBack, onHome = onHome) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                placeholder = { Text("Поиск по термину или описанию") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedTopic == null,
                    onClick = { selectedTopic = null },
                    label = { Text("Все") }
                )
                LibraryData.topics.forEach { t ->
                    FilterChip(
                        selected = selectedTopic == t,
                        onClick = { selectedTopic = t },
                        label = { Text(t) }
                    )
                }
            }

            Text(
                "Найдено: ${filtered.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(
                    items = filtered,
                    key = { "${it.topic}:${it.term}" }
                ) { item ->
                    LibraryCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun LibraryCard(item: LibraryItem) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                item.topic,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                item.term,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 2
            )

            if (expanded && item.example != null) {
                HorizontalDivider()
                Text("Пример", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(item.example, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
